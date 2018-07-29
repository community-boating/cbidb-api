package IO

sealed abstract class CommitType

case object COMMIT_TYPE_DO extends CommitType
case object COMMIT_TYPE_ASSERT_NO_ACTION extends CommitType
case object COMMIT_TYPE_SKIP extends CommitType
