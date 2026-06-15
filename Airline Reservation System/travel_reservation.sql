DROP DATABASE IF EXISTS travel_reservation;
CREATE DATABASE travel_reservation;
USE travel_reservation;

DROP TABLE IF EXISTS Users;
CREATE TABLE Users (
    uid CHAR(15) PRIMARY KEY NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('admin', 'rep', 'customer') NOT NULL
);

DROP TABLE IF EXISTS Customers;
CREATE TABLE Customers (
    cid CHAR(15) PRIMARY KEY  NOT NULL,
    uid CHAR(15) UNIQUE NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    dob DATE,
    FOREIGN KEY (uid) REFERENCES Users(uid)
        ON DELETE CASCADE
);

DROP TABLE IF EXISTS Employees;
CREATE TABLE Employees (
    eid CHAR(15) PRIMARY KEY NOT NULL,
    uid CHAR(15) UNIQUE NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    role ENUM('admin', 'rep') NOT NULL,
    FOREIGN KEY (uid) REFERENCES Users(uid)
        ON DELETE CASCADE
);

DROP TABLE IF EXISTS Airlines;
CREATE TABLE Airlines (
    aid CHAR(2) PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(50),
    email VARCHAR(100)
);

DROP TABLE IF EXISTS Airports;
CREATE TABLE Airports (
    apid CHAR(3) PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    street VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(30),
    country VARCHAR(50)
);

DROP TABLE IF EXISTS Aircrafts;
CREATE TABLE Aircrafts (
    acid CHAR(15) NOT NULL,
    aid CHAR(2) NOT NULL,
    model VARCHAR(100),
    economy_seats INT NOT NULL DEFAULT 0,
    business_seats INT NOT NULL DEFAULT 0,
    first_seats INT NOT NULL DEFAULT 0,
    PRIMARY KEY (aid, acid),
    FOREIGN KEY (aid) REFERENCES Airlines(aid)
        ON DELETE CASCADE
);

DROP TABLE IF EXISTS Flights;
CREATE TABLE Flights (
    fid CHAR(15) NOT NULL,
    aid CHAR(2) NOT NULL,
    acid CHAR(15) NOT NULL,
    flight_number VARCHAR(10) NOT NULL,
    d_apid CHAR(3) NOT NULL,
    a_apid CHAR(3) NOT NULL,
    depart_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    flight_type ENUM('domestic', 'international') NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (fid, aid),
    FOREIGN KEY (aid, acid) REFERENCES Aircrafts(aid, acid),
    FOREIGN KEY (d_apid) REFERENCES Airports(apid),
    FOREIGN KEY (a_apid) REFERENCES Airports(apid),
    UNIQUE (aid, flight_number)
);

DROP TABLE IF EXISTS Flight_Days;
CREATE TABLE Flight_Days (
    fid CHAR(15) NOT NULL,
    aid CHAR(2) NOT NULL,
    day ENUM('Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun') NOT NULL,
    PRIMARY KEY (fid, aid, day),
    FOREIGN KEY (fid, aid) REFERENCES Flights(fid, aid)
        ON DELETE CASCADE
);

DROP TABLE IF EXISTS Flight_Instances;
CREATE TABLE Flight_Instances (
    instance_id  INT AUTO_INCREMENT PRIMARY KEY,
    fid CHAR(15) NOT NULL,
    aid CHAR(2)  NOT NULL,
    flight_date DATE NOT NULL,
    seats_available INT NOT NULL CHECK (seats_available >= 0), 
    FOREIGN KEY (fid, aid) REFERENCES Flights(fid, aid),
    UNIQUE (fid, aid, flight_date)
);

DROP TABLE IF EXISTS Tickets;
CREATE TABLE Tickets (
    tid CHAR(15) PRIMARY KEY NOT NULL,
    cid CHAR(15) NOT NULL,
    ticket_type ENUM('one-way', 'round-trip') NOT NULL,
    total_fare DECIMAL(10,2) NOT NULL,
    booking_fee DECIMAL(10,2) NOT NULL,
    purchase_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('booked', 'canceled', 'waitlisted', 'completed') DEFAULT 'booked',
    FOREIGN KEY (cid) REFERENCES Customers(cid)
        ON DELETE CASCADE
);

ALTER TABLE Tickets
MODIFY status ENUM('booked', 'reserved', 'canceled', 'waitlisted', 'completed');

DROP TABLE IF EXISTS Ticket_Flights;
CREATE TABLE Ticket_Flights (
    tid CHAR(15) NOT NULL,
    instance_id  INT NOT NULL,
    seat_number VARCHAR(10),
    seat_class ENUM('economy', 'business', 'first') NOT NULL,
    meal VARCHAR(50),
    segment_order INT NOT NULL,
    PRIMARY KEY (tid, segment_order),
    FOREIGN KEY (tid) REFERENCES Tickets(tid)
        ON DELETE CASCADE,
    FOREIGN KEY (instance_id) REFERENCES Flight_Instances(instance_id)
        ON DELETE CASCADE
);

DROP TABLE IF EXISTS Waiting_List;
CREATE TABLE Waiting_List (
    waitlist_id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    cid CHAR(15) NOT NULL,
    instance_id  INT NOT NULL,
    request_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cid) REFERENCES Customers(cid)
        ON DELETE CASCADE,
    FOREIGN KEY (instance_id) REFERENCES Flight_Instances(instance_id)
        ON DELETE CASCADE,
    UNIQUE (cid, instance_id)
);


INSERT INTO Airlines (aid, name, country, email)
VALUES ('AA', 'American Airlines', 'USA', 'americanairlines@aa.com'),
('UA', 'United Airlines', 'USA', 'unitedairlines@united.com'),
('DL', 'Delta Airlines', 'USA', 'deltaairlines@delta.com'),
('BA', 'British Airways', 'UK', 'britishairways@ba.com'),
('EK', 'Emirates', 'UAE', 'emirateairlines@emirates.com'),
('SW', 'Southwest Airlines', 'USA', 'sw582@southwest.com'),
('JB', 'JetBlue Airways', 'USA', 'jba336@jetblue.com'),
('AS', 'Alaska Airlines', 'USA', 'ala5921@alaskaair.com'),
('NK', 'Spirit Airlines', 'USA', 'sairlines417@spirit.com'),
('FR', 'Frontier Airlines', 'USA', 'ffa269@flyfrontier.com'),
('AC', 'Air Canada', 'Canada', 'ac381@aircanada.ca'),
('LH', 'Lufthansa', 'Germany', 'lufg6488@lufthansa.com'),
('AF', 'Air France', 'France', 'af7791@airfrance.com'),
('KL', 'KLM Royal Dutch Airlines', 'Netherlands', 'royaldutch49@klm.com'),
('QR', 'Qatar Airways', 'Qatar', 'qa369@qatarairways.com'),
('SQ', 'Singapore Airlines', 'Singapore', 'sin745@singaporeair.com'),
('CX', 'Cathay Pacific', 'Hong Kong', 'cp594@cathaypacific.com'),
('NH', 'All Nippon Airways', 'Japan', 'alln955@ana.co.jp'),
('QF', 'Qantas Airways', 'Australia', 'qa3177@qantas.com'),
('AI', 'Air India', 'India', 'ai884@airindia.com'),
('TK', 'Turkish Airlines', 'Turkey', 'turair@thy.com'),
('EY', 'Etihad Airways', 'UAE', 'ea279@etihad.com'),
('AZ', 'ITA Airways', 'Italy', 'ita64@itaairways.com'),
('IB', 'Iberia', 'Spain', 'ibs@iberia.com'),
('SK', 'Scandinavian Airlines', 'Sweden', 'sca@flysas.com');


INSERT INTO Airports (apid, name, street, city, state, country)
VALUES ('JFK', 'John F. Kennedy International Airport', 'JFK Access Rd', 'New York', 'NY', 'USA'),
('EWR', 'Newark Liberty International Airport', '3 Brewster Rd', 'Newark', 'NJ', 'USA'),
('LGA', 'LaGuardia Airport', '94-00 Grand Central Pkwy', 'New York', 'NY', 'USA'),
('LAX', 'Los Angeles International Airport', '1 World Way', 'Los Angeles', 'CA', 'USA'),
('ORD', 'Chicago O''Hare International Airport', '10000 W O''Hare Ave', 'Chicago', 'IL', 'USA'),
('DFW', 'Dallas/Fort Worth International Airport', '2400 Aviation Dr', 'Dallas', 'TX', 'USA'),
('DEN', 'Denver International Airport', '8500 Peña Blvd', 'Denver', 'CO', 'USA'),
('SFO', 'San Francisco International Airport', 'San Francisco Intl Airport', 'San Francisco', 'CA', 'USA'),
('SEA', 'Seattle-Tacoma International Airport', '17801 International Blvd', 'Seattle', 'WA', 'USA'),
('MIA', 'Miami International Airport', '2100 NW 42nd Ave', 'Miami', 'FL', 'USA'),
('ATL', 'Hartsfield-Jackson Atlanta International Airport', '6000 N Terminal Pkwy', 'Atlanta', 'GA', 'USA'),
('BOS', 'Logan International Airport', '1 Harborside Dr', 'Boston', 'MA', 'USA'),
('PHX', 'Phoenix Sky Harbor International Airport', '3400 E Sky Harbor Blvd', 'Phoenix', 'AZ', 'USA'),
('IAH', 'George Bush Intercontinental Airport', '2800 N Terminal Rd', 'Houston', 'TX', 'USA'),
('LAS', 'Harry Reid International Airport', '5757 Wayne Newton Blvd', 'Las Vegas', 'NV', 'USA'),
('MSP', 'Minneapolis-Saint Paul International Airport', '4300 Glumack Dr', 'Minneapolis', 'MN', 'USA'),
('DTW', 'Detroit Metropolitan Wayne County Airport', '1 Detroit Metro Airport', 'Detroit', 'MI', 'USA'),
('CLT', 'Charlotte Douglas International Airport', '5501 Josh Birmingham Pkwy', 'Charlotte', 'NC', 'USA'),
('PHL', 'Philadelphia International Airport', '8500 Essington Ave', 'Philadelphia', 'PA', 'USA'),
('SLC', 'Salt Lake City International Airport', '776 N Terminal Dr', 'Salt Lake City', 'UT', 'USA'),
('DCA', 'Ronald Reagan Washington National Airport', '2401 Ronald Reagan Washington National Airport Access Rd', 'Arlington', 'VA', 'USA'),
('BWI', 'Baltimore/Washington International Airport', '7050 Friendship Rd', 'Baltimore', 'MD', 'USA'),
('YYZ', 'Toronto Pearson International Airport', '6301 Silver Dart Dr', 'Toronto', 'ON', 'Canada'),
('LHR', 'Heathrow Airport', 'Longford TW6', 'London', 'N/A', 'UK'),
('CDG', 'Charles de Gaulle Airport', '95700 Roissy-en-France', 'Paris', 'N/A', 'France'),
('DXB', 'Dubai International Airport', 'Dubai Airport Road', 'Dubai', 'N/A', 'UAE'),
('SYD', 'Sydney Kingsford Smith Airport', 'Airport Drive', 'Sydney', 'NSW', 'Australia');

INSERT INTO Aircrafts (acid, aid, model, economy_seats, business_seats, first_seats)
VALUES ('N123AA', 'AA', 'Boeing 737', 120, 30, 10),
('N456UA', 'UA', 'Airbus A320', 110, 25, 15),
('N789DL', 'DL', 'Boeing 757', 140, 30, 10),
('GBA001', 'BA', 'Boeing 777', 200, 50, 25),
('UAE001', 'EK', 'Airbus A380', 300, 70, 50),
('N321AA', 'AA', 'Airbus A321', 150, 30, 10),
('N654UA', 'UA', 'Boeing 737 MAX', 130, 25, 10),
('N987DL', 'DL', 'Airbus A321', 160, 35, 15),
('GBA002', 'BA', 'Airbus A320', 140, 30, 10),
('UAE002', 'EK', 'Boeing 777', 250, 60, 40),
('SW001', 'SW', 'Boeing 737', 175, 0, 0),
('JB001', 'JB', 'Airbus A320', 150, 20, 0),
('AS001', 'AS', 'Boeing 737', 160, 20, 10),
('NK001', 'NK', 'Airbus A321', 180, 0, 0),
('FR001', 'FR', 'Airbus A320', 170, 0, 0),
('AC001', 'AC', 'Boeing 787', 210, 35, 20),
('LH001', 'LH', 'Airbus A350', 220, 40, 25),
('AF001', 'AF', 'Boeing 777', 240, 50, 30),
('KL001', 'KL', 'Boeing 787', 200, 35, 25),
('QR001', 'QR', 'Airbus A350', 230, 45, 30),
('SQ001', 'SQ', 'Airbus A380', 300, 60, 40),
('CX001', 'CX', 'Boeing 777', 250, 45, 30),
('NH001', 'NH', 'Boeing 787', 210, 40, 20),
('QF001', 'QF', 'Airbus A330', 220, 35, 25),
('AI001', 'AI', 'Boeing 787', 200, 30, 20);


INSERT INTO Users (uid, username, password, role)
VALUES ('U001', 'john_smith', 'js2354', 'admin'),
('U002', 'esperanza_nguyen', 'en935', 'customer'),
('U003', 'sara_olsen', 'so782', 'customer'),
('U004', 'ariya_rojas', 'ar439', 'rep'),
('U005', 'caden_hayes', 'ch5588', 'rep'),
('U006', 'mark_doe', 'md3951', 'customer'),
('U007', 'xavier_skylark', 'xs3305', 'admin'),
('U008', 'brent_campbell', 'bc7408', 'rep'),
('U009', 'alex_raymond', 'ar4762', 'customer'),
('U010', 'ava_jenner', 'aj1184', 'customer'),
('U011', 'lorenzo_parker', 'lp8721', 'customer'),
('U012', 'matilda_james', 'mj7144', 'customer'),
('U013', 'nile_worth', 'nw6632', 'customer'),
('U014', 'oliver_barker', 'ob3291', 'customer'),
('U015', 'eliza_dawson', 'ed1289', 'customer'),
('U016', 'siliya_moore', 'sm4088', 'rep'),
('U017', 'logan_adams', 'la5537', 'rep'),
('U018', 'ira_miller', 'im7112', 'customer'),
('U019', 'luther_taito', 'lt9941', 'customer'),
('U020', 'amanda_thomas', 'at4732', 'admin'),
('U021', 'helen_jimenez', 'hj6173', 'rep'),
('U022', 'caroline_white', 'cw8550', 'customer'),
('U023', 'brian_hall', 'bh3845', 'customer'),
('U024', 'elizabeth_manfort', 'em7991', 'rep'),
('U025', 'deborah_tarantino', 'dt9012', 'customer');


INSERT INTO Customers (cid, uid, first_name, last_name, email, phone, dob)
VALUES ('C001', 'U002', 'Esperanza', 'Nguyen', 'espn59@gmail.com', '9086348924','1994-08-27'),
('C002', 'U003', 'Sara', 'Olsen', 'sarao274@gmail.com','9086548984', '1999-02-16'),
('C003', 'U006', 'Mark', 'Doe', 'markd466@gmail.com', '9081548784','2005-01-21'),
('C004', 'U009', 'Alex', 'Raymond','alray519@gmail.com', '9089448764', '2003-05-30'),
('C005', 'U010', 'Ava', 'Jenner','avaj622@gmail.com', '9082745184', '1988-11-01'),
('C006', 'U011', 'Lorenzo', 'Parker', 'lorenzo.p@gmail.com', '9081112233', '1997-03-12'),
('C007', 'U012', 'Matilda', 'James', 'matilda.j@gmail.com', '9082223344', '1996-07-18'),
('C008', 'U013', 'Nile', 'Worth', 'nile.w@gmail.com', '9083334455', '2000-01-09'),
('C009', 'U014', 'Oliver', 'Barker', 'oliver.b@gmail.com', '9084445566', '1995-10-22'),
('C010', 'U015', 'Eliza', 'Dawson', 'eliza.d@gmail.com', '9085556677', '1998-12-30'),
('C011', 'U018', 'Ira', 'Miller', 'ira.m@gmail.com', '9086667788', '2001-06-14'),
('C012', 'U019', 'Luther', 'Taito', 'luther.t@gmail.com', '9087778899', '1994-04-05'),
('C013', 'U022', 'Caroline', 'White', 'caroline.w@gmail.com', '9088889900', '1999-09-09'),
('C014', 'U023', 'Brian', 'Hall', 'brian.h@gmail.com', '9089990011', '1993-11-11'),
('C015', 'U025', 'Deborah', 'Tarantino', 'deborah.t@gmail.com', '9081212121', '2002-02-02');


INSERT INTO Employees (eid, uid, first_name, last_name, role)
VALUES ('E001', 'U001', 'John', 'Smith', 'admin'),
('E002', 'U004', 'Ariya', 'Rojas', 'rep'),
('E003', 'U005', 'Caden', 'Hayes', 'rep'),
('E004','U007', 'Xavier', 'Skylark', 'admin'),
('E005', 'U008', 'Brent', 'Campbell', 'rep'),
('E006', 'U016', 'Siliya', 'Moore', 'rep'),
('E007', 'U017', 'Logan', 'Adams', 'rep'),
('E008', 'U020', 'Amanda', 'Thomas', 'admin'),
('E009', 'U021', 'Helen', 'Jimenez', 'rep'),
('E010', 'U024', 'Elizabeth', 'Manfort', 'rep');


INSERT INTO Flights (fid, aid, acid, flight_number, d_apid, a_apid, depart_time, arrival_time, flight_type, base_price)
VALUES ('AA101', 'AA', 'N123AA', 'AA101', 'JFK', 'LAX', '08:00:00', '11:15:00', 'domestic', 300.00),
('UA202', 'UA', 'N456UA', 'UA202', 'EWR', 'ORD', '09:30:00', '11:00:00', 'domestic', 250.00),
('DL303', 'DL', 'N789DL', 'DL303', 'LGA', 'JFK', '13:45:00', '16:30:00', 'domestic', 200.00),
('BA404', 'BA', 'GBA001', 'BA404', 'JFK', 'EWR', '18:45:00', '06:30:00', 'international', 600.00),
('EK505', 'EK', 'UAE001', 'EK505', 'ORD', 'LGA', '22:00:00', '19:30:00', 'international', 900.00),
('AA102', 'AA', 'N321AA', 'AA102', 'JFK', 'SFO', '07:00:00', '10:30:00', 'domestic', 750.00),
('AA103', 'AA', 'N123AA', 'AA103', 'LAX', 'JFK', '12:00:00', '20:30:00', 'domestic', 840.00),
('UA203', 'UA', 'N654UA', 'UA203', 'EWR', 'LAX', '08:15:00', '11:45:00', 'domestic', 580.00),
('UA204', 'UA', 'N456UA', 'UA204', 'ORD', 'SEA', '14:00:00', '16:30:00', 'domestic', 460.00),
('DL304', 'DL', 'N987DL', 'DL304', 'LGA', 'MIA', '09:00:00', '12:00:00', 'domestic', 280.00),
('DL305', 'DL', 'N789DL', 'DL305', 'ATL', 'JFK', '17:00:00', '20:00:00', 'domestic', 940.00),
('BA405', 'BA', 'GBA002', 'BA405', 'JFK', 'LHR', '18:00:00', '06:00:00', 'international', 950.00),
('BA406', 'BA', 'GBA001', 'BA406', 'LHR', 'LAX', '20:00:00', '10:30:00', 'international', 600.00),
('EK506', 'EK', 'UAE002', 'EK506', 'ORD', 'DXB', '22:30:00', '18:00:00', 'international', 850.00),
('EK507', 'EK', 'UAE001', 'EK507', 'DXB', 'JFK', '23:45:00', '07:30:00', 'international', 880.00),
('SW601', 'SW', 'SW001', 'SW601', 'DEN', 'LAS', '06:30:00', '07:45:00', 'domestic', 280.00),
('JB701', 'JB', 'JB001', 'JB701', 'BOS', 'MIA', '07:15:00', '10:45:00', 'domestic', 310.00),
('AS801', 'AS', 'AS001', 'AS801', 'SEA', 'SFO', '09:30:00', '11:30:00', 'domestic', 200.00),
('NK901', 'NK', 'NK001', 'NK901', 'LGA', 'ORD', '13:00:00', '14:30:00', 'domestic', 340.00),
('AC1001', 'AC', 'AC001', 'AC1001', 'JFK', 'YYZ', '10:00:00', '12:00:00', 'international', 540.00),
('LH1101', 'LH', 'LH001', 'LH1101', 'ORD', 'CDG', '18:30:00', '08:00:00', 'international', 900.00),
('AF1201', 'AF', 'AF001', 'AF1201', 'JFK', 'CDG', '19:00:00', '08:15:00', 'international', 690.00),
('QR1301', 'QR', 'QR001', 'QR1301', 'LAX', 'DXB', '21:00:00', '19:00:00', 'international', 950.00),
('SQ1401', 'SQ', 'SQ001', 'SQ1401', 'SFO', 'LHR', '22:00:00', '16:00:00', 'international', 1240.00),
('QF1501', 'QF', 'QF001', 'QF1501', 'LAX', 'SYD', '23:00:00', '07:00:00', 'international', 1500.00);

INSERT INTO Flight_Days (fid, aid, day)
VALUES
('AA101', 'AA', 'Mon'),
('UA202', 'UA', 'Tue'),
('DL303', 'DL', 'Fri'),
('BA404', 'BA', 'Thu'),
('EK505', 'EK', 'Sun'),
('AA102', 'AA', 'Wed'),
('AA103', 'AA', 'Sun'),
('UA203', 'UA', 'Thu'),
('UA204', 'UA', 'Sat'),
('DL304', 'DL', 'Mon'),
('DL305', 'DL', 'Wed'),
('BA405', 'BA', 'Sun'),
('BA406', 'BA', 'Fri'),
('EK506', 'EK', 'Mon'),
('EK507', 'EK', 'Thu'),
('SW601', 'SW', 'Mon'),
('JB701', 'JB', 'Tue'),
('AS801', 'AS', 'Wed'),
('NK901', 'NK', 'Fri'),
('AC1001', 'AC', 'Sat'),
('LH1101', 'LH', 'Thu'),
('AF1201', 'AF', 'Sun'),
('QR1301', 'QR', 'Tue'),
('SQ1401', 'SQ', 'Wed'),
('QF1501', 'QF', 'Thu');



INSERT INTO Flight_Instances (fid, aid, flight_date, seats_available)
VALUES ('AA101', 'AA', '2026-05-10', 120),
('UA202', 'UA', '2026-05-12', 100),
('DL303', 'DL', '2026-05-15', 90),
('BA404', 'BA', '2026-05-18', 200),
('EK505', 'EK', '2026-05-20', 250),
('AA101', 'AA', '2026-05-21', 115),
('AA102', 'AA', '2026-05-22', 140),
('AA103', 'AA', '2026-05-15', 130),
('UA202', 'UA', '2026-05-24', 95),
('UA203', 'UA', '2026-05-25', 110),
('UA204', 'UA', '2026-05-26', 105),
('DL303', 'DL', '2026-05-27', 85),
('DL304', 'DL', '2026-05-28', 90),
('DL305', 'DL', '2026-05-29', 88),
('BA404', 'BA', '2026-05-30', 200),
('BA405', 'BA', '2026-05-31', 195),
('BA406', 'BA', '2026-06-01', 190),
('EK505', 'EK', '2026-06-02', 250),
('EK506', 'EK', '2026-06-03', 240),
('EK507', 'EK', '2026-06-04', 245),
('SW601', 'SW', '2026-06-05', 160),
('JB701', 'JB', '2026-06-06', 150),
('AS801', 'AS', '2026-06-07', 155),
('NK901', 'NK', '2026-06-08', 170),
('AC1001', 'AC', '2026-06-09', 200),
('LH1101', 'LH', '2026-06-10', 210),
('AF1201', 'AF', '2026-06-11', 220),
('QR1301', 'QR', '2026-06-12', 230),
('SQ1401', 'SQ', '2026-06-13', 300),
('QF1501', 'QF', '2026-06-14', 280);


INSERT INTO Waiting_List (cid, instance_id, request_time)
VALUES
('C001', 1, '2026-05-01 09:00:00'),
('C002', 2, '2026-05-01 09:05:00'),
('C003', 3, '2026-05-01 09:10:00'),
('C004', 4, '2026-05-01 09:15:00'),
('C005', 5, '2026-05-01 09:20:00'),
('C006', 1, '2026-05-02 09:00:00'),
('C007', 1, '2026-05-02 09:10:00'),
('C008', 2, '2026-05-02 09:20:00');



INSERT INTO Tickets (tid, cid, ticket_type, total_fare, booking_fee, purchase_time, status)
VALUES ('T001', 'C001', 'one-way', 250.00, 25.00, '2026-04-29 09:30:00', 'booked'),
('T002', 'C002', 'round-trip', 480.00, 35.00, '2026-04-29 10:15:00', 'booked'),
('T003', 'C001', 'one-way', 300.00, 25.00, '2026-04-29 11:00:00', 'waitlisted'),
('T004', 'C002', 'round-trip', 550.00, 40.00, '2026-04-29 12:45:00', 'booked'),
('T005', 'C003', 'one-way', 220.00, 20.00, '2026-04-29 14:10:00', 'canceled'),
('T006', 'C006', 'one-way', 260.00, 20.00, '2026-04-30 09:00:00', 'booked'),
('T007', 'C007', 'round-trip', 500.00, 35.00, '2026-04-30 09:10:00', 'booked'),
('T008', 'C008', 'one-way', 280.00, 25.00, '2026-04-30 09:20:00', 'waitlisted'),
('T009', 'C009', 'round-trip', 600.00, 40.00, '2026-04-30 09:30:00', 'booked'),
('T010', 'C010', 'one-way', 220.00, 20.00, '2026-04-30 09:40:00', 'booked'),
('T011', 'C011', 'one-way', 300.00, 25.00, '2026-04-30 09:50:00', 'booked'),
('T012', 'C012', 'round-trip', 450.00, 30.00, '2026-04-30 10:00:00', 'booked'),
('T013', 'C013', 'one-way', 270.00, 20.00, '2026-04-30 10:10:00', 'canceled'),
('T014', 'C014', 'round-trip', 700.00, 50.00, '2026-04-30 10:20:00', 'booked'),
('T015', 'C015', 'one-way', 230.00, 20.00, '2026-04-30 10:30:00', 'booked'),
('T016', 'C001', 'round-trip', 520.00, 35.00, '2026-04-30 10:40:00', 'booked'),
('T017', 'C002', 'one-way', 310.00, 25.00, '2026-04-30 10:50:00', 'booked'),
('T018', 'C003', 'round-trip', 480.00, 30.00, '2026-04-30 11:00:00', 'booked'),
('T019', 'C004', 'one-way', 260.00, 20.00, '2026-04-30 11:10:00', 'booked'),
('T020', 'C005', 'round-trip', 650.00, 40.00, '2026-04-30 11:20:00', 'booked'),
('T021', 'C006', 'one-way', 240.00, 20.00, '2026-04-30 11:30:00', 'booked'),
('T022', 'C007', 'round-trip', 580.00, 35.00, '2026-04-30 11:40:00', 'booked'),
('T023', 'C008', 'one-way', 275.00, 25.00, '2026-04-30 11:50:00', 'waitlisted'),
('T024', 'C009', 'round-trip', 720.00, 50.00, '2026-04-30 12:00:00', 'booked'),
('T025', 'C010', 'one-way', 210.00, 20.00, '2026-04-30 12:10:00', 'booked');



INSERT INTO Ticket_Flights (tid, instance_id, seat_number, seat_class, meal, segment_order)
VALUES ('T001', 1, '12A', 'economy', 'Vegetarian', 1),
('T002', 2, '4C', 'business', 'Standard', 1),
('T003', 3, '18F', 'economy', 'Kosher', 1),
('T004', 4, '1A', 'first', 'Halal', 1),
('T005', 5, '22D', 'economy', 'Vegan', 1),
('T006', 1, '10A', 'economy', 'Vegetarian', 1),
('T007', 2, '3B', 'business', 'Standard', 1),
('T008', 3, '18C', 'economy', 'Kosher', 1),
('T009', 4, '1A', 'first', 'Halal', 1),
('T010', 5, '22D', 'economy', 'Vegan', 1),
('T011', 6, '12B', 'economy', 'Standard', 1),
('T012', 7, '5C', 'business', 'Vegetarian', 1),
('T013', 8, '19A', 'economy', 'Kosher', 1),
('T014', 9, '2A', 'first', 'Halal', 1),
('T015', 10, '21D', 'economy', 'Vegan', 1),
('T016', 11, '9A', 'economy', 'Standard', 1),
('T017', 12, '6B', 'business', 'Vegetarian', 1),
('T018', 13, '14C', 'economy', 'Kosher', 1),
('T019', 14, '8D', 'economy', 'Halal', 1),
('T020', 15, '1B', 'first', 'Vegan', 1),
('T021', 16, '10C', 'economy', 'Standard', 1),
('T022', 17, '4A', 'business', 'Vegetarian', 1),
('T023', 18, '20F', 'economy', 'Kosher', 1),
('T024', 19, '3A', 'first', 'Halal', 1),
('T025', 20, '17D', 'economy', 'Vegan', 1);


INSERT INTO Flights (
    fid, aid, acid, flight_number,
    d_apid, a_apid,
    depart_time, arrival_time,
    flight_type, base_price
)
VALUES
('AA201', 'AA', 'N123AA', 'AA201', 'JFK', 'MIA', '09:00:00', '12:00:00', 'domestic', 320.00),
('AA202', 'AA', 'N123AA', 'AA202', 'MIA', 'JFK', '13:30:00', '16:30:00', 'domestic', 320.00),

('UA301', 'UA', 'N456UA', 'UA301', 'EWR', 'DEN', '08:00:00', '10:30:00', 'domestic', 400.00),
('UA302', 'UA', 'N456UA', 'UA302', 'DEN', 'EWR', '11:30:00', '16:00:00', 'domestic', 400.00),

('DL401', 'DL', 'N789DL', 'DL401', 'LGA', 'ATL', '07:30:00', '10:00:00', 'domestic', 280.00),
('DL402', 'DL', 'N789DL', 'DL402', 'ATL', 'LGA', '18:00:00', '20:30:00', 'domestic', 280.00);

INSERT INTO Flight_Instances (fid, aid, flight_date, seats_available)
VALUES
-- Round Trip 1 (JFK ↔ MIA)
('AA201', 'AA', '2026-04-05', 0),
('AA202', 'AA', '2026-04-10', 0),

-- Round Trip 2 (EWR ↔ DEN)
('UA301', 'UA', '2026-04-07', 0),
('UA302', 'UA', '2026-04-12', 0),

-- Round Trip 3 (LGA ↔ ATL)
('DL401', 'DL', '2026-04-08', 0),
('DL402', 'DL', '2026-04-13', 0);

INSERT INTO Tickets (tid, cid, ticket_type, total_fare, booking_fee, purchase_time, status)
VALUES
('TP100', 'C001', 'round-trip', 640.00, 50.00, '2026-04-01 10:00:00', 'completed'),
('TP101', 'C002', 'round-trip', 800.00, 50.00, '2026-04-02 11:00:00', 'completed'),
('TP102', 'C003', 'round-trip', 560.00, 40.00, '2026-04-03 12:00:00', 'completed');


INSERT INTO Ticket_Flights (tid, instance_id, seat_number, seat_class, meal, segment_order)
VALUES
-- TP100 (JFK ↔ MIA)
('TP100', 31, '12A', 'economy', 'Standard', 1),
('TP100', 32, '14A', 'economy', 'Standard', 2),

-- TP101 (EWR ↔ DEN)
('TP101', 33, '4B', 'business', 'Vegetarian', 1),
('TP101', 34, '5B', 'business', 'Vegetarian', 2),

-- TP102 (LGA ↔ ATL)
('TP102', 35, '18C', 'economy', 'Halal', 1),
('TP102', 36, '19C', 'economy', 'Halal', 2);


INSERT INTO Flights (
    fid, aid, acid, flight_number,
    d_apid, a_apid,
    depart_time, arrival_time,
    flight_type, base_price
)
VALUES
('AA301', 'AA', 'N123AA', 'AA301', 'JFK', 'LAX', '06:30:00', '09:45:00', 'domestic', 310.00),
('AA302', 'AA', 'N321AA', 'AA302', 'JFK', 'LAX', '12:15:00', '15:40:00', 'domestic', 360.00),
('AA303', 'AA', 'N123AA', 'AA303', 'JFK', 'LAX', '19:00:00', '22:20:00', 'domestic', 420.00),

('DL501', 'DL', 'N789DL', 'DL501', 'JFK', 'LAX', '07:45:00', '11:05:00', 'domestic', 330.00),
('DL502', 'DL', 'N987DL', 'DL502', 'JFK', 'LAX', '14:30:00', '17:55:00', 'domestic', 390.00),

('JB901', 'JB', 'JB001', 'JB901', 'JFK', 'LAX', '09:00:00', '12:25:00', 'domestic', 295.00),
('JB902', 'JB', 'JB001', 'JB902', 'JFK', 'LAX', '16:45:00', '20:15:00', 'domestic', 345.00);


INSERT INTO Flight_Days (fid, aid, day)
VALUES
('AA301', 'AA', 'Thu'), ('AA301', 'AA', 'Fri'), ('AA301', 'AA', 'Sat'), ('AA301', 'AA', 'Sun'),
('AA302', 'AA', 'Thu'), ('AA302', 'AA', 'Fri'), ('AA302', 'AA', 'Sat'), ('AA302', 'AA', 'Sun'),
('AA303', 'AA', 'Thu'), ('AA303', 'AA', 'Fri'), ('AA303', 'AA', 'Sat'), ('AA303', 'AA', 'Sun'),

('DL501', 'DL', 'Thu'), ('DL501', 'DL', 'Fri'), ('DL501', 'DL', 'Sat'), ('DL501', 'DL', 'Sun'),
('DL502', 'DL', 'Thu'), ('DL502', 'DL', 'Fri'), ('DL502', 'DL', 'Sat'), ('DL502', 'DL', 'Sun'),

('JB901', 'JB', 'Thu'), ('JB901', 'JB', 'Fri'), ('JB901', 'JB', 'Sat'), ('JB901', 'JB', 'Sun'),
('JB902', 'JB', 'Thu'), ('JB902', 'JB', 'Fri'), ('JB902', 'JB', 'Sat'), ('JB902', 'JB', 'Sun');



INSERT INTO Flight_Instances (fid, aid, flight_date, seats_available)
VALUES
-- 2026-05-07
('AA301', 'AA', '2026-05-07', 120),
('AA302', 'AA', '2026-05-07', 80),
('DL501', 'DL', '2026-05-07', 95),
('JB901', 'JB', '2026-05-07', 60),

-- 2026-05-08
('AA301', 'AA', '2026-05-08', 115),
('AA303', 'AA', '2026-05-08', 90),
('DL502', 'DL', '2026-05-08', 75),
('JB902', 'JB', '2026-05-08', 50),

-- 2026-05-09
('AA302', 'AA', '2026-05-09', 100),
('AA303', 'AA', '2026-05-09', 70),
('DL501', 'DL', '2026-05-09', 85),
('JB901', 'JB', '2026-05-09', 40),

-- 2026-05-10
('AA301', 'AA', '2026-05-10', 120),
('AA302', 'AA', '2026-05-10', 110),
('AA303', 'AA', '2026-05-10', 0),
('DL501', 'DL', '2026-05-10', 90),
('DL502', 'DL', '2026-05-10', 65),
('JB901', 'JB', '2026-05-10', 30),
('JB902', 'JB', '2026-05-10', 0),

-- 2026-05-11
('AA301', 'AA', '2026-05-11', 118),
('AA302', 'AA', '2026-05-11', 105),
('DL501', 'DL', '2026-05-11', 88),
('DL502', 'DL', '2026-05-11', 70),
('JB901', 'JB', '2026-05-11', 35),

-- 2026-05-12
('AA301', 'AA', '2026-05-12', 100),
('AA303', 'AA', '2026-05-12', 95),
('DL501', 'DL', '2026-05-12', 75),
('JB902', 'JB', '2026-05-12', 45),

-- 2026-05-13
('AA302', 'AA', '2026-05-13', 95),
('AA303', 'AA', '2026-05-13', 80),
('DL502', 'DL', '2026-05-13', 60),
('JB901', 'JB', '2026-05-13', 25),

-- 2026-05-14
('AA301', 'AA', '2026-05-14', 90),
('AA302', 'AA', '2026-05-14', 85),
('DL501', 'DL', '2026-05-14', 70),
('JB902', 'JB', '2026-05-14', 20),

-- 2026-05-15
('AA301', 'AA', '2026-05-15', 80),
('AA302', 'AA', '2026-05-15', 75),
('AA303', 'AA', '2026-05-15', 0),
('DL501', 'DL', '2026-05-15', 65),
('DL502', 'DL', '2026-05-15', 55),
('JB901', 'JB', '2026-05-15', 15),
('JB902', 'JB', '2026-05-15', 0),

-- 2026-05-16
('AA301', 'AA', '2026-05-16', 70),
('AA303', 'AA', '2026-05-16', 60),
('DL502', 'DL', '2026-05-16', 50),
('JB901', 'JB', '2026-05-16', 10),

-- 2026-05-17
('AA302', 'AA', '2026-05-17', 65),
('AA303', 'AA', '2026-05-17', 55),
('DL501', 'DL', '2026-05-17', 45),
('JB902', 'JB', '2026-05-17', 5),

-- 2026-05-18
('AA301', 'AA', '2026-05-18', 60),
('AA302', 'AA', '2026-05-18', 50),
('DL501', 'DL', '2026-05-18', 40),
('JB901', 'JB', '2026-05-18', 0);



INSERT INTO Flights (
    fid, aid, acid, flight_number,
    d_apid, a_apid,
    depart_time, arrival_time,
    flight_type, base_price
)
VALUES
('AA104', 'AA', 'N321AA', 'AA104', 'LAX', 'JFK', '08:00:00', '16:30:00', 'domestic', 820.00),
('AA105', 'AA', 'N123AA', 'AA105', 'LAX', 'JFK', '17:00:00', '01:30:00', 'domestic', 880.00),

('DL306', 'DL', 'N987DL', 'DL306', 'LAX', 'JFK', '09:30:00', '18:00:00', 'domestic', 840.00),
('DL307', 'DL', 'N789DL', 'DL307', 'LAX', 'JFK', '15:30:00', '00:00:00', 'domestic', 870.00),

('JB703', 'JB', 'JB001', 'JB703', 'LAX', 'JFK', '10:00:00', '18:30:00', 'domestic', 780.00),
('JB704', 'JB', 'JB001', 'JB704', 'LAX', 'JFK', '19:30:00', '04:00:00', 'domestic', 800.00);


INSERT INTO Flight_Instances (fid, aid, flight_date, seats_available)
VALUES
-- 2026-05-12
('AA103','AA','2026-05-12',100),
('AA104','AA','2026-05-12',90),
('DL306','DL','2026-05-12',80),
('JB703','JB','2026-05-12',50),

-- 2026-05-13
('AA103','AA','2026-05-13',90),
('AA105','AA','2026-05-13',0),
('DL307','DL','2026-05-13',70),
('JB704','JB','2026-05-13',0),

-- 2026-05-14
('AA104','AA','2026-05-14',85),
('AA105','AA','2026-05-14',75),
('DL306','DL','2026-05-14',65),
('JB703','JB','2026-05-14',30),

-- 2026-05-15
('AA104','AA','2026-05-15',80),
('AA105','AA','2026-05-15',0),
('DL306','DL','2026-05-15',60),
('DL307','DL','2026-05-15',55),
('JB703','JB','2026-05-15',25),
('JB704','JB','2026-05-15',0),

-- 2026-05-16
('AA103','AA','2026-05-16',60),
('AA105','AA','2026-05-16',50),
('DL307','DL','2026-05-16',45),
('JB703','JB','2026-05-16',20),

-- 2026-05-17
('AA104','AA','2026-05-17',55),
('AA105','AA','2026-05-17',45),
('DL306','DL','2026-05-17',40),
('JB704','JB','2026-05-17',10),

-- 2026-05-18
('AA103','AA','2026-05-18',50),
('AA104','AA','2026-05-18',40),
('DL306','DL','2026-05-18',35),
('JB703','JB','2026-05-18',15);
