-- MySQL dump 10.13  Distrib 5.7.19, for Linux (x86_64)
--
-- Host: localhost    Database: cbi
-- ------------------------------------------------------
-- Server version	5.7.19-0ubuntu0.16.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `AP_CLASS_FORMATS`
--

DROP TABLE IF EXISTS `AP_CLASS_FORMATS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AP_CLASS_FORMATS` (
  `FORMAT_ID` int(11) DEFAULT NULL,
  `TYPE_ID` int(11) DEFAULT NULL,
  `DESCRIPTION` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AP_CLASS_FORMATS`
--

LOCK TABLES `AP_CLASS_FORMATS` WRITE;
/*!40000 ALTER TABLE `AP_CLASS_FORMATS` DISABLE KEYS */;
/*!40000 ALTER TABLE `AP_CLASS_FORMATS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AP_CLASS_INSTANCES`
--

DROP TABLE IF EXISTS `AP_CLASS_INSTANCES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AP_CLASS_INSTANCES` (
  `INSTANCE_ID` int(11) DEFAULT NULL,
  `FORMAT_ID` int(11) DEFAULT NULL,
  `LOCATION_STRING` varchar(250) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AP_CLASS_INSTANCES`
--

LOCK TABLES `AP_CLASS_INSTANCES` WRITE;
/*!40000 ALTER TABLE `AP_CLASS_INSTANCES` DISABLE KEYS */;
/*!40000 ALTER TABLE `AP_CLASS_INSTANCES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AP_CLASS_SESSIONS`
--

DROP TABLE IF EXISTS `AP_CLASS_SESSIONS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AP_CLASS_SESSIONS` (
  `SESSION_ID` int(11) DEFAULT NULL,
  `INSTANCE_ID` int(11) DEFAULT NULL,
  `SESSION_DATETIME` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AP_CLASS_SESSIONS`
--

LOCK TABLES `AP_CLASS_SESSIONS` WRITE;
/*!40000 ALTER TABLE `AP_CLASS_SESSIONS` DISABLE KEYS */;
/*!40000 ALTER TABLE `AP_CLASS_SESSIONS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AP_CLASS_SIGNUPS`
--

DROP TABLE IF EXISTS `AP_CLASS_SIGNUPS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AP_CLASS_SIGNUPS` (
  `SIGNUP_ID` int(11) DEFAULT NULL,
  `INSTANCE_ID` int(11) DEFAULT NULL,
  `SIGNUP_TYPE` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AP_CLASS_SIGNUPS`
--

LOCK TABLES `AP_CLASS_SIGNUPS` WRITE;
/*!40000 ALTER TABLE `AP_CLASS_SIGNUPS` DISABLE KEYS */;
/*!40000 ALTER TABLE `AP_CLASS_SIGNUPS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AP_CLASS_TYPES`
--

DROP TABLE IF EXISTS `AP_CLASS_TYPES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AP_CLASS_TYPES` (
  `TYPE_ID` int(11) DEFAULT NULL,
  `TYPE_NAME` varchar(50) DEFAULT NULL,
  `DISPLAY_ORDER` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AP_CLASS_TYPES`
--

LOCK TABLES `AP_CLASS_TYPES` WRITE;
/*!40000 ALTER TABLE `AP_CLASS_TYPES` DISABLE KEYS */;
/*!40000 ALTER TABLE `AP_CLASS_TYPES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CLASS_INSTRUCTORS`
--

DROP TABLE IF EXISTS `CLASS_INSTRUCTORS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CLASS_INSTRUCTORS` (
  `INSTRUCTOR_ID` int(11) DEFAULT NULL,
  `NAME_FIRST` varchar(100) DEFAULT NULL,
  `NAME_LAST` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CLASS_INSTRUCTORS`
--

LOCK TABLES `CLASS_INSTRUCTORS` WRITE;
/*!40000 ALTER TABLE `CLASS_INSTRUCTORS` DISABLE KEYS */;
/*!40000 ALTER TABLE `CLASS_INSTRUCTORS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CLASS_LOCATIONS`
--

DROP TABLE IF EXISTS `CLASS_LOCATIONS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CLASS_LOCATIONS` (
  `LOCATION_ID` int(11) DEFAULT NULL,
  `LOCATION_NAME` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CLASS_LOCATIONS`
--

LOCK TABLES `CLASS_LOCATIONS` WRITE;
/*!40000 ALTER TABLE `CLASS_LOCATIONS` DISABLE KEYS */;
/*!40000 ALTER TABLE `CLASS_LOCATIONS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `JP_CLASS_INSTANCES`
--

DROP TABLE IF EXISTS `JP_CLASS_INSTANCES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JP_CLASS_INSTANCES` (
  `INSTANCE_ID` int(11) DEFAULT NULL,
  `INSTRUCTOR_ID` int(11) DEFAULT NULL,
  `LOCATION_ID` int(11) DEFAULT NULL,
  `TYPE_ID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `JP_CLASS_INSTANCES`
--

LOCK TABLES `JP_CLASS_INSTANCES` WRITE;
/*!40000 ALTER TABLE `JP_CLASS_INSTANCES` DISABLE KEYS */;
/*!40000 ALTER TABLE `JP_CLASS_INSTANCES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `JP_CLASS_SESSIONS`
--

DROP TABLE IF EXISTS `JP_CLASS_SESSIONS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JP_CLASS_SESSIONS` (
  `SESSION_ID` int(11) DEFAULT NULL,
  `INSTANCE_ID` int(11) DEFAULT NULL,
  `SESSION_DATETIME` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `JP_CLASS_SESSIONS`
--

LOCK TABLES `JP_CLASS_SESSIONS` WRITE;
/*!40000 ALTER TABLE `JP_CLASS_SESSIONS` DISABLE KEYS */;
/*!40000 ALTER TABLE `JP_CLASS_SESSIONS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `JP_CLASS_SIGNUPS`
--

DROP TABLE IF EXISTS `JP_CLASS_SIGNUPS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JP_CLASS_SIGNUPS` (
  `SIGNUP_ID` int(11) DEFAULT NULL,
  `INSTANCE_ID` int(11) DEFAULT NULL,
  `SIGNUP_TYPE` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `JP_CLASS_SIGNUPS`
--

LOCK TABLES `JP_CLASS_SIGNUPS` WRITE;
/*!40000 ALTER TABLE `JP_CLASS_SIGNUPS` DISABLE KEYS */;
/*!40000 ALTER TABLE `JP_CLASS_SIGNUPS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `JP_CLASS_TYPES`
--

DROP TABLE IF EXISTS `JP_CLASS_TYPES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JP_CLASS_TYPES` (
  `TYPE_ID` int(11) DEFAULT NULL,
  `TYPE_NAME` varchar(100) DEFAULT NULL,
  `DISPLAY_ORDER` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `JP_CLASS_TYPES`
--

LOCK TABLES `JP_CLASS_TYPES` WRITE;
/*!40000 ALTER TABLE `JP_CLASS_TYPES` DISABLE KEYS */;
/*!40000 ALTER TABLE `JP_CLASS_TYPES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `JP_TEAMS`
--

DROP TABLE IF EXISTS `JP_TEAMS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JP_TEAMS` (
  `TEAM_ID` int(11) DEFAULT NULL,
  `TEAM_NAME` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `JP_TEAMS`
--

LOCK TABLES `JP_TEAMS` WRITE;
/*!40000 ALTER TABLE `JP_TEAMS` DISABLE KEYS */;
/*!40000 ALTER TABLE `JP_TEAMS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `JP_TEAM_EVENT_POINTS`
--

DROP TABLE IF EXISTS `JP_TEAM_EVENT_POINTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JP_TEAM_EVENT_POINTS` (
  `ROW_ID` int(11) DEFAULT NULL,
  `TEAM_ID` int(11) DEFAULT NULL,
  `POINTS` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `JP_TEAM_EVENT_POINTS`
--

LOCK TABLES `JP_TEAM_EVENT_POINTS` WRITE;
/*!40000 ALTER TABLE `JP_TEAM_EVENT_POINTS` DISABLE KEYS */;
/*!40000 ALTER TABLE `JP_TEAM_EVENT_POINTS` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-08-21 21:33:52
