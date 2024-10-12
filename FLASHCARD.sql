Create database Flashcard
use Flashcard
go
CREATE TABLE Account
(
  Username VARCHAR(255) NOT NULL,
  Password VARCHAR(255) NOT NULL,
  Email VARCHAR(255) NOT NULL,
  Phone VARCHAR(255) NOT NULL,
  PRIMARY KEY (Username)
);

CREATE TABLE Word
(
  Name VARCHAR(255) NOT NULL,
  Meaning VARCHAR(255) NOT NULL,
  Pronounce VARCHAR(255) NOT NULL,
  IsMarked INT NOT NULL,
  State VARCHAR(255) NOT NULL,
  IdTopic INT NOT NULL,
  PRIMARY KEY (Name)
);

CREATE TABLE Topic
(
  Name VARCHAR(255) NOT NULL,
  IdTopic INT NOT NULL,
  Access VARCHAR(255) NOT NULL,
  State VARCHAR(255) NOT NULL,
  CreateTime DATE NOT NULL,
  IdUser INT NOT NULL,
  Username VARCHAR(255) NOT NULL,
  PRIMARY KEY (IdTopic),
  FOREIGN KEY (Username) REFERENCES Account(Username)
);

CREATE TABLE Folder
(
  Name VARCHAR(255) NOT NULL,
  IdFolder INT NOT NULL,
  PRIMARY KEY (IdFolder)
);

CREATE TABLE Topic_Folder
(
  IdTopic INT NOT NULL,
  IdFolder INT NOT NULL,
  PRIMARY KEY (IdTopic, IdFolder),
  FOREIGN KEY (IdTopic) REFERENCES Topic(IdTopic),
  FOREIGN KEY (IdFolder) REFERENCES Folder(IdFolder)
);

CREATE TABLE Topic_Word
(
  IdTopic INT NOT NULL,
  Name VARCHAR(255) NOT NULL,
  PRIMARY KEY (IdTopic, Name),
  FOREIGN KEY (IdTopic) REFERENCES Topic(IdTopic),
  FOREIGN KEY (Name) REFERENCES Word(Name)
);