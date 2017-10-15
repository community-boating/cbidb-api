package Reporting.ReportingFilters

import Reporting.Report.FILTER_MAP
import Services.PersistenceBroker
import Storable.StorableClass

// SomeNoArgFilter:%(ApClassInstanceType:7|ApClassInstanceType:8)%ApClassInstanceYear:2017
class ReportingFilterSpecParser[T <: StorableClass](pb: PersistenceBroker) {
  case class Token(c: Char) {
    def char: Char = c
  }

  object CONTROL_CHARS {
    val START_EXPR = Token('(')
    val END_EXPR = Token( ')')
    val EQUALS = Token(':')
    val EOL = Token('$')
    val AND = Token('%')
    val OR = Token('|')
  }

  def getControlChars: Set[Char] = Set(
    // TODO: redo with reflection
    CONTROL_CHARS.START_EXPR.char,
    CONTROL_CHARS.END_EXPR.char,
    CONTROL_CHARS.EQUALS.char,
    CONTROL_CHARS.EOL.char,
    CONTROL_CHARS.AND.char,
    CONTROL_CHARS.OR.char
  )

  sealed trait Mode
  sealed trait Operation extends Mode
  case object FILTER_NAME extends Mode
  case object ARG extends Mode
  case object MODE_EQUALS extends Mode
  case object COLLECTING_RECURSE extends Mode

  def parse(spec: String): ReportingFilter[T] = {
    var mode: Mode = FILTER_NAME
    var sb = new StringBuilder
    var filterName: String = ""
    var currentOperator: Option[Token] = None
    var filterList: List[ReportingFilter[T]] = List()
    var endExprCharactersNeeded: Int = 0

    (spec + CONTROL_CHARS.EOL.char).toCharArray.map(c => Token(c)).foreach(t => t match {
      case CONTROL_CHARS.START_EXPR => mode match {
        case COLLECTING_RECURSE => endExprCharactersNeeded += 1 // nested (
        case FILTER_NAME => mode = COLLECTING_RECURSE  // open a () to recurse its contents
        case _ => throw new BadReportingFilterSpecException("Found an erroneous (")
      }
      case CONTROL_CHARS.END_EXPR => mode match{
        case COLLECTING_RECURSE => {
          if (endExprCharactersNeeded > 0) {
            // still within a nested ()
            endExprCharactersNeeded -= 1
            sb.append(t.char)
          } else {
            // Recurse!
            filterList = parse(sb.toString()) :: filterList
            sb = new StringBuilder
          }
        }
        case _ => throw new BadReportingFilterSpecException("Found an erroneous )")
      }
      case CONTROL_CHARS.EQUALS => mode match {
        case COLLECTING_RECURSE => sb.append(t.char)
        case FILTER_NAME => {
          mode = MODE_EQUALS
          filterName = sb.toString()  // dump sb to filterName...
          sb = new StringBuilder()    // ... and reset sb
        }
        case _ => throw new BadReportingFilterSpecException
      }
      case CONTROL_CHARS.AND | CONTROL_CHARS.OR => mode match {
        case COLLECTING_RECURSE => sb.append(t.char)
        case MODE_EQUALS | ARG => {
          currentOperator match {
            case None => currentOperator = Some(t)
            case Some(oc: Token) => if (oc != t) throw new BadReportingFilterSpecException("Different operators found in the same expression")
          }
          // finish the current filter...
          filterList = getFilter(filterName, sb.toString()) :: filterList
          // ... and reset everything for a new one
          mode = FILTER_NAME
          sb = new StringBuilder
          filterName = ""
        }
        case _ => throw new BadReportingFilterSpecException
      }
      case CONTROL_CHARS.EOL =>
        if (filterName.length > 0)
          filterList = getFilter(filterName, sb.toString()) :: filterList
      case Token(c: Char) => {
        sb.append(c)
        mode match {
          case MODE_EQUALS => {
            mode = ARG
          }
          case _ =>
        }
      }
    })

    if (filterList.length == 1) filterList.head
    else if (filterList.length > 1) currentOperator match {
      case Some(CONTROL_CHARS.AND) => filterList.reduceLeft((a, b) => a and b)
      case Some(CONTROL_CHARS.OR) => filterList.reduceLeft((a, b) => a or b)
      case Some(Token(c: Char)) => throw new BadReportingFilterSpecException("Stored a token as operator that is not an operator: " + c)
      case None => throw new BadReportingFilterSpecException("Found multiple filters with no combinator")
    }
    else throw new BadReportingFilterSpecException("No filters could be created")
  }

  private def getFilter(filterName: String, filterArgs: String): ReportingFilter[T] = {
    val filterClass: Class[ReportingFilter[T]] = FILTER_MAP(filterName).asInstanceOf[Class[ReportingFilter[T]]]
    val rawArgs: Array[String] = filterArgs.split(",")
    val args: Array[_ <: Object] = Array(pb) ++ rawArgs
    filterClass.getConstructors()(0).newInstance(args: _*).asInstanceOf[ReportingFilter[T]]
  }

  class BadReportingFilterSpecException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
  ) extends Exception(message, cause)
}
