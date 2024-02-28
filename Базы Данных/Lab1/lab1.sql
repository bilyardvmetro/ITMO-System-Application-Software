CREATE TABLE ImaginationTypes (Id SERIAL PRIMARY KEY, Name VARCHAR(20));
CREATE TABLE Post (Id SERIAL PRIMARY KEY, Name VARCHAR(30));
CREATE TABLE People (Id SERIAL PRIMARY KEY, Name VARCHAR(20), Sex VARCHAR(1), ImaginationID INT REFERENCES imaginationtypes(Id), PostID INT REFERENCES post(Id));
CREATE TABLE Action (Id SERIAL PRIMARY KEY, Title VARCHAR(20), PersonID INT REFERENCES people(Id), Characteristic TEXT);
CREATE TABLE ResearchTypes (Id SERIAL PRIMARY KEY, Title VARCHAR(20));
CREATE TABLE Laboratories (Id SERIAL PRIMARY KEY, Name VARCHAR(50), ResearchTypeID INT REFERENCES researchtypes(id));
CREATE TABLE ActionsInLabs (ActionID INT REFERENCES action(id), LabID INT REFERENCES laboratories(id));

INSERT INTO imaginationtypes(name) VALUES ('богатое');

INSERT INTO post (name) VALUES ('лаборант');
INSERT INTO post (name) VALUES ('санитар');

INSERT INTO researchtypes (title) VALUES ('биологические');
INSERT INTO researchtypes (title) VALUES ('хирургические');

INSERT INTO people (name, sex, imaginationID, PostID) VALUES ('Стоун', 'M', 1, 1);
INSERT INTO people (name, sex, imaginationID, PostID) VALUES ('Элис', 'F', 1, 1);
INSERT INTO people (name, sex, imaginationID, PostID) VALUES ('Санитар', 'M', 1, 2);

INSERT INTO laboratories (name, researchTypeID) VALUES ('Лаборатория ИТМО', 1);
INSERT INTO laboratories (name, researchTypeID) VALUES ('Хирургическая лаборатория', 2);

INSERT INTO action (title, personID, characteristic) VALUES ('Качать головой', 1, 'продолжал отрицательно качать головой');
INSERT INTO action (title, personID, characteristic) VALUES ('Преследовать', 3, 'санитар из хирургического отделения преследовал Элис');

INSERT INTO actionsinlabs (actionid, labid) VALUES (1, 1);
INSERT INTO actionsinlabs (actionid, labid) VALUES (2, 2);
