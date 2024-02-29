CREATE TABLE ImaginationTypes (Id INT PRIMARY KEY, Name VARCHAR(20));
CREATE TABLE Post (Id INT PRIMARY KEY, Name VARCHAR(30));
CREATE TABLE People (Id INT PRIMARY KEY, Name VARCHAR(20), Sex VARCHAR(1), ImaginationID INT REFERENCES imaginationtypes(Id));
CREATE TABLE Actions (Id INT PRIMARY KEY, Title VARCHAR(20), PersonID INT REFERENCES people(Id), IsImaginary BOOLEAN, Characteristic TEXT, BeginTime TIMESTAMP, EndTime TIMESTAMP);
CREATE TABLE ResearchTypes (Id INT PRIMARY KEY, Title VARCHAR(20));
CREATE TABLE Laboratories (Id SERIAL PRIMARY KEY, Name VARCHAR(50), ResearchTypeID INT REFERENCES researchtypes(id));
CREATE TABLE ActionsInLabs (ActionID INT REFERENCES actions(id), LabID INT REFERENCES laboratories(id));
CREATE TABLE Workers (Id INT PRIMARY KEY, PersonID INT REFERENCES people(Id), EmploymentBegin TIMESTAMP, EmploymentEnd TIMESTAMP, PostID INT REFERENCES post(Id);
CREATE TABLE WorkersInLabs (WorkerID INT REFERENCES People(id), LabID INT REFERENCES laboratories(id));

INSERT INTO imaginationtypes(name) VALUES ('богатое');
INSERT INTO imaginationtypes(name) VALUES ('скудное');
INSERT INTO imaginationtypes(name) VALUES ('обычное');

INSERT INTO post (name) VALUES ('лаборант');
INSERT INTO post (name) VALUES ('санитар');

INSERT INTO researchtypes (title) VALUES ('биологические');
INSERT INTO researchtypes (title) VALUES ('хирургические');

INSERT INTO people (name, sex, imaginationID) VALUES ('Стоун', 'M', 3);
INSERT INTO people (name, sex, imaginationID) VALUES ('Элис', 'F', 1);
INSERT INTO people (name, sex, imaginationID) VALUES ('Санитар', 'M', 2);

INSERT INTO laboratories (name, researchTypeID) VALUES ('Лаборатория ИТМО', 1);
INSERT INTO laboratories (name, researchTypeID) VALUES ('Хирургическая лаборатория', 2);

INSERT INTO actions (title, personID, characteristic, IsImaginary, BeginTime, EndTime) VALUES ('Качать головой', 1, 'продолжал отрицательно качать головой', FALSE, '2024-02-23 11:40:45', '2024-02-23 11:42:45');
INSERT INTO actions (title, personID, characteristic, IsImaginary, BeginTime, EndTime) VALUES ('Утверждать', 2, 'утверждала о преследовании её санитаром', FALSE, '2024-02-21 13:40:34', '2024-02-21 13:41:03');
INSERT INTO actions (title, personID, characteristic, IsImaginary, BeginTime, EndTime) VALUES ('Преследовать', 3, 'санитар из хирургического отделения преследовал Элис', TRUE, '2024-02-18 18:20:23', '2024-02-19 20:22:56');

INSERT INTO actionsinlabs (actionid, labid) VALUES (1, 1);
INSERT INTO actionsinlabs (actionid, labid) VALUES (2, 1);
INSERT INTO actionsinlabs (actionid, labid) VALUES (3, 2);

INSERT INTO Workers (PersonID, EmploymentBegin, EmploymentEnd, PostID) VALUES (1, '2012-02-23 11:40:45', '2036-02-23 11:42:45', 1);
INSERT INTO Workers (PersonID, EmploymentBegin, EmploymentEnd, PostID) VALUES (2, '2014-02-23 11:40:45', '2048-02-23 11:42:45', 1);
INSERT INTO Workers (PersonID, EmploymentBegin, EmploymentEnd, PostID) VALUES (3, '2020-02-23 11:40:45', '2056-02-23 11:42:45', 2);

INSERT INTO WorkersInLabs (WorkerID, labid) VALUES (1, 1);
INSERT INTO WorkersInLabs (WorkerID, labid) VALUES (2, 1);
INSERT INTO WorkersInLabs (WorkerID, labid) VALUES (3, 2);
