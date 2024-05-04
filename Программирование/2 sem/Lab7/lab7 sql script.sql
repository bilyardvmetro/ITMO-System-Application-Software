CREATE TABLE users (
id SERIAL PRIMARY KEY,
username VARCHAR(90) UNIQUE,
hashedPassword VARCHAR(90)
);

CREATE TABLE vehicles (
id SERIAL PRIMARY KEY,
name VARCHAR(80),
x FLOAT,
y FLOAT,
creationDate TIMESTAMP,
enginePower FLOAT,
capacity FLOAT,
distanceTravelled FLOAT,
vehicleType VARCHAR(10),
creatorID INT REFERENCES users(id)
);