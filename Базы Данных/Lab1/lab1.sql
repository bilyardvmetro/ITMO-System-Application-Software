CREATE TABLE ImaginationTypes (Id SERIAL PRIMARY KEY, Name VARCHAR(20));
CREATE TABLE Post (Id SERIAL PRIMARY KEY, Name VARCHAR(30));
CREATE TABLE People (Id SERIAL PRIMARY KEY, Name VARCHAR(20), Sex VARCHAR(1), ImaginationID INT REFERENCES imaginationtypes(Id), PostID INT REFERENCES post(Id));
CREATE TABLE Actions (Id SERIAL PRIMARY KEY, Title VARCHAR(20), PersonID INT REFERENCES people(Id), Characteristic TEXT);
CREATE TABLE ResearchTypes (Id SERIAL PRIMARY KEY, Title VARCHAR(20));
CREATE TABLE Laboratories (Id SERIAL PRIMARY KEY, Name VARCHAR(50), ResearchTypeID INT REFERENCES researchtypes(id));
CREATE TABLE ActionsInLabs (ActionID INT REFERENCES action(id), LabID INT REFERENCES laboratories(id), BeginTime TIMESTAMP, EndTime TIMESTAMP);

INSERT INTO imaginationtypes(name) VALUES ('богатое');
INSERT INTO imaginationtypes(name) VALUES ('скудное');
INSERT INTO imaginationtypes(name) VALUES ('обычное');

INSERT INTO post (name) VALUES ('лаборант');
INSERT INTO post (name) VALUES ('санитар');

INSERT INTO researchtypes (title) VALUES ('биологические');
INSERT INTO researchtypes (title) VALUES ('хирургические');

INSERT INTO people (name, sex, imaginationID, PostID) VALUES ('Стоун', 'M', 3, 1);
INSERT INTO people (name, sex, imaginationID, PostID) VALUES ('Элис', 'F', 1, 1);
INSERT INTO people (name, sex, imaginationID, PostID) VALUES ('Санитар', 'M', 2, 2);

INSERT INTO laboratories (name, researchTypeID) VALUES ('Лаборатория ИТМО', 1);
INSERT INTO laboratories (name, researchTypeID) VALUES ('Хирургическая лаборатория', 2);

INSERT INTO actions (title, personID, characteristic) VALUES ('Качать головой', 1, 'продолжал отрицательно качать головой');
INSERT INTO actions (title, personID, characteristic) VALUES ('Утверждать', 2, 'утверждала о преследовании её санитаром');
INSERT INTO actions (title, personID, characteristic) VALUES ('Преследовать', 3, 'санитар из хирургического отделения преследовал Элис');

INSERT INTO actionsinlabs (actionid, labid, BeginTime, EndTime) VALUES (1, 1, '2024-02-23 11:40:45', '2024-02-23 11:42:45');
INSERT INTO actionsinlabs (actionid, labid, BeginTime, EndTime) VALUES (2, 1, '2024-02-21 13:40:34', '2024-02-21 13:41:03');
INSERT INTO actionsinlabs (actionid, labid, BeginTime, EndTime) VALUES (3, 2, '2024-02-18 18:20:23', '2024-02-19 20:22:56');
