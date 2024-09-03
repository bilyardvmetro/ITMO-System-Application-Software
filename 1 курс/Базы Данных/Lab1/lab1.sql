DROP TABLE IF EXISTS ActionsInLabs CASCADE;
DROP TABLE IF EXISTS WorkersInLabs CASCADE;
DROP TABLE IF EXISTS Actions CASCADE;
DROP TABLE IF EXISTS People CASCADE;
DROP TABLE IF EXISTS Workers CASCADE;
DROP TABLE IF EXISTS Laboratories CASCADE;
DROP TABLE IF EXISTS ImaginationTypes CASCADE;
DROP TABLE IF EXISTS Post CASCADE;
DROP TABLE IF EXISTS ResearchTypes CASCADE;

CREATE TABLE ImaginationTypes (Id INT PRIMARY KEY, Name VARCHAR(20));
CREATE TABLE Post (Id INT PRIMARY KEY, Name VARCHAR(30));
CREATE TABLE People (Id INT PRIMARY KEY, Name VARCHAR(20), Sex VARCHAR(1), ImaginationID INT REFERENCES imaginationtypes(Id));
CREATE TABLE Actions (Id INT PRIMARY KEY, Title VARCHAR(20), PersonID INT REFERENCES people(Id), IsImaginary BOOLEAN, Characteristic TEXT, BeginTime TIMESTAMP, EndTime TIMESTAMP);
CREATE TABLE ResearchTypes (Id INT PRIMARY KEY, Title VARCHAR(20));
CREATE TABLE Laboratories (Id INT PRIMARY KEY, Name VARCHAR(50), ResearchTypeID INT REFERENCES researchtypes(id));
CREATE TABLE ActionsInLabs (ActionID INT REFERENCES actions(id), LabID INT REFERENCES laboratories(id));
CREATE TABLE Workers (Id INT PRIMARY KEY, PersonID INT REFERENCES people(Id), EmploymentBegin TIMESTAMP, EmploymentEnd TIMESTAMP, PostID INT REFERENCES post(Id));
CREATE TABLE WorkersInLabs (WorkerID INT REFERENCES People(id), LabID INT REFERENCES laboratories(id));

INSERT INTO imaginationtypes(id, name) VALUES (1, 'богатое');
INSERT INTO imaginationtypes(id, name) VALUES (2, 'скудное');
INSERT INTO imaginationtypes(id, name) VALUES (3, 'обычное');

INSERT INTO post (id, name) VALUES (1, 'лаборант');
INSERT INTO post (id, name) VALUES (2, 'санитар');

INSERT INTO researchtypes (id, title) VALUES (1, 'биологические');
INSERT INTO researchtypes (id, title) VALUES (2, 'хирургические');

INSERT INTO people (id, name, sex, imaginationID) VALUES (1, 'Стоун', 'M', 3);
INSERT INTO people (id, name, sex, imaginationID) VALUES (2, 'Элис', 'F', 1);
INSERT INTO people (id, name, sex, imaginationID) VALUES (3, 'Санитар', 'M', 2);

INSERT INTO laboratories (id, name, researchTypeID) VALUES (1, 'Лаборатория ИТМО', 1);
INSERT INTO laboratories (id, name, researchTypeID) VALUES (2, 'Хирургическая лаборатория', 2);

INSERT INTO actions (id, title, personID, characteristic, IsImaginary, BeginTime, EndTime) VALUES (1, 'Качать головой', 1, 'продолжал отрицательно качать головой', FALSE, '2024-02-23 11:40:45', '2024-02-23 11:42:45');
INSERT INTO actions (id, title, personID, characteristic, IsImaginary, BeginTime, EndTime) VALUES (2, 'Утверждать', 2, 'утверждала о преследовании её санитаром', FALSE, '2024-02-21 13:40:34', '2024-02-21 13:41:03');
INSERT INTO actions (id, title, personID, characteristic, IsImaginary, BeginTime, EndTime) VALUES (3, 'Преследовать', 3, 'санитар из хирургического отделения преследовал Элис', TRUE, '2024-02-18 18:20:23', '2024-02-19 20:22:56');

INSERT INTO actionsinlabs (actionid, labid) VALUES (1, 1);
INSERT INTO actionsinlabs (actionid, labid) VALUES (2, 1);
INSERT INTO actionsinlabs (actionid, labid) VALUES (3, 2);

INSERT INTO Workers (id, PersonID, EmploymentBegin, EmploymentEnd, PostID) VALUES (1, 1, '2012-02-23 11:40:45', '2036-02-23 11:42:45', 1);
INSERT INTO Workers (id, PersonID, EmploymentBegin, EmploymentEnd, PostID) VALUES (2, 2, '2014-02-23 11:40:45', '2048-02-23 11:42:45', 1);
INSERT INTO Workers (id, PersonID, EmploymentBegin, EmploymentEnd, PostID) VALUES (3, 3, '2020-02-23 11:40:45', '2056-02-23 11:42:45', 2);

INSERT INTO WorkersInLabs (WorkerID, labid) VALUES (1, 1);
INSERT INTO WorkersInLabs (WorkerID, labid) VALUES (2, 1);
INSERT INTO WorkersInLabs (WorkerID, labid) VALUES (3, 2);
