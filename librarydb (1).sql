-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 05, 2025 at 10:46 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `librarydb`
--

-- --------------------------------------------------------

--
-- Table structure for table `books`
--

CREATE TABLE `books` (
  `book_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `category` varchar(100) NOT NULL,
  `copies` int(11) NOT NULL CHECK (`copies` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `books`
--

INSERT INTO `books` (`book_id`, `title`, `author`, `category`, `copies`) VALUES
(1, 'Effective JAVA', 'Joshua Bloch', 'JAVA', 4),
(2, 'The Pragmatic Programmer', 'Andrew Hunt', 'Software development', 8),
(3, 'Plant Pathology', 'George N. Agrios', 'Plant Disease', 7),
(4, 'Hotel Front Office Management', 'James A. Bardi', 'Hotel Operations', 7),
(5, 'Educational Psychology', 'Anita Woolfolk', 'Psychology of Learning', 3),
(6, 'Principles of Criminal Law', 'Wayne R. Lafave', 'Criminal Law', 5),
(7, 'Principles of Accounting', 'Marian Powers', 'Financial Accounting', 2);

-- --------------------------------------------------------

--
-- Table structure for table `borrowings`
--

CREATE TABLE `borrowings` (
  `borrowing_id` int(11) NOT NULL,
  `student_number` varchar(20) NOT NULL,
  `book_id` int(11) NOT NULL,
  `copies_borrowed` int(11) NOT NULL,
  `date_borrowed` datetime NOT NULL DEFAULT current_timestamp(),
  `return_date` datetime DEFAULT NULL,
  `status` varchar(20) DEFAULT 'Borrowed'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `borrowings`
--

INSERT INTO `borrowings` (`borrowing_id`, `student_number`, `book_id`, `copies_borrowed`, `date_borrowed`, `return_date`, `status`) VALUES
(2, '22-30239', 1, 0, '2025-06-04 18:46:46', '2025-06-04 18:47:41', 'Returned'),
(3, '22-30241', 3, 3, '2025-06-05 15:34:40', NULL, 'Borrowed');

-- --------------------------------------------------------

--
-- Table structure for table `members`
--

CREATE TABLE `members` (
  `member_id` int(11) NOT NULL,
  `student_number` varchar(50) NOT NULL,
  `firstname` varchar(100) NOT NULL,
  `lastname` varchar(100) NOT NULL,
  `mi` varchar(10) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `year` varchar(20) DEFAULT NULL,
  `program` varchar(100) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `members`
--

INSERT INTO `members` (`member_id`, `student_number`, `firstname`, `lastname`, `mi`, `gender`, `year`, `program`, `address`) VALUES
(1, '22-30239', 'Althea', 'Carrera', 'A', 'Female', '3rd', 'BSIT', 'Santa Ana, Cagayan'),
(2, '22-30241', 'Remy Dianne', 'Ventura', 'I', 'Female', '3rd', 'BSIT', 'Santa Ana, Cagayan'),
(3, '22-30254', 'Mary Grace', 'Dutdut', 'V', 'Female', '2nd', 'BSCRIM', 'Gonzaga, Cagayan'),
(4, '22-30514', 'Jasmin Kate', 'Los Banos', 'A', 'Female', '4th', 'BSAIS', 'Gonzaga, Cagayan');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `books`
--
ALTER TABLE `books`
  ADD PRIMARY KEY (`book_id`);

--
-- Indexes for table `borrowings`
--
ALTER TABLE `borrowings`
  ADD PRIMARY KEY (`borrowing_id`),
  ADD KEY `fk_student` (`student_number`),
  ADD KEY `fk_book` (`book_id`);

--
-- Indexes for table `members`
--
ALTER TABLE `members`
  ADD PRIMARY KEY (`member_id`),
  ADD UNIQUE KEY `student_number` (`student_number`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `books`
--
ALTER TABLE `books`
  MODIFY `book_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `borrowings`
--
ALTER TABLE `borrowings`
  MODIFY `borrowing_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `members`
--
ALTER TABLE `members`
  MODIFY `member_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `borrowings`
--
ALTER TABLE `borrowings`
  ADD CONSTRAINT `fk_book` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_student` FOREIGN KEY (`student_number`) REFERENCES `members` (`student_number`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
