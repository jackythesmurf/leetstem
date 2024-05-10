-- import to SQLite by running: sqlite3.exe db.sqlite3 -init sqlite.sql

PRAGMA journal_mode = MEMORY;
PRAGMA synchronous = OFF;
PRAGMA foreign_keys = OFF;
PRAGMA ignore_check_constraints = OFF;
PRAGMA auto_vacuum = NONE;
PRAGMA secure_delete = OFF;
BEGIN TRANSACTION;
/*
Sample data insertion
*/
INSERT INTO "question" VALUES
                           (9448,15232,3,1,2,'{ "body_image": "/api/temp/sample/qbody.png", "num_blanks": 4 }', '{76}{55}{62}{29}{32}', 'Sample Fill-in-the-Blanks #9481852', '2023-09-09 04:48:45'),
                           (9449,15232,3,1,2,'{ "body_image": "/api/temp/sample/qbody.png", "num_blanks": 5 }', '{39}{58}{22}{82}{19}', 'Sample Fill-in-the-Blanks #6839511', '2023-09-09 04:50:00'),
                           (9450,15232,3,1,2,'{ "body_image": "/api/temp/sample/qbody.png", "num_blanks": 1 }', '{93}{53}{3}{47}{42}', 'Sample Fill-in-the-Blanks #4147074', '2023-09-09 04:51:15');
INSERT INTO "user" VALUES
                       (1,'user250296','/api/temp/sample/avatar.png','2023-09-28 08:47:19'),
                       (2,'user953884','/api/temp/sample/avatar.png','2023-09-28 12:32:01'),
                       (3,'user049135','/api/temp/sample/avatar.png','2023-09-28 15:00:42');
INSERT INTO "user_auth" VALUES
                            (1,1,'s1@test.com','$2a$10$seB1thQFo62IsQ6R3tRIc.qIJ.khnXQMeNIm2CEwEjNI/.TlZt9jO'),  -- password is 'Password123!@#'
                            (2,2,'s2@test.com','$2a$10$I0TjyNqXGb8zSJKXydo2e.2ECIdK4jW6zDW/3t/ZiX2zTFazZT3J2'),  -- password is 'Password123!@#'
                            (3,3,'t1@test.com','$2a$10$xzm50LY1QoGPvaIL27T.JOZ3vrSixVWi31DMBezYX/NLwClrmonsi');  -- password is 'Password123!@#'
INSERT INTO "user_role" VALUES
                            (1,1,0),
                            (2,2,0),
                            (3,3,1);
INSERT INTO "user_detail" VALUES
                              (1,1,NULL),
                              (2,2,NULL),
                              (3,3,NULL);
INSERT INTO "user_subject" VALUES
                               (1,1,15030,1),
                               (2,1,15050,1),
                               (3,1,15080,0),
                               (4,1,15120,0),
                               (5,1,15210,1),
                               (6,1,15236,1),
                               (7,1,15255,1),
                               (8,1,15250,1),
                               (9,1,15260,0),
                               (10,1,15330,1),
                               (11,1,15345,0),
                               (12,1,15360,0),
                               (13,1,27398,1),
                               (14,1,15232,1),
                               (15,2,15030,0),
                               (16,2,15050,0),
                               (17,2,15080,0),
                               (18,2,15120,0),
                               (19,2,15210,0),
                               (20,2,15236,0),
                               (21,2,15255,0),
                               (22,2,15250,0),
                               (23,2,15260,0),
                               (24,2,15330,0),
                               (25,2,15345,0),
                               (26,2,15360,0),
                               (27,2,27398,0),
                               (28,2,15232,0),
                               (29,3,15030,0),
                               (30,3,15050,0),
                               (31,3,15080,0),
                               (32,3,15120,0),
                               (33,3,15210,0),
                               (34,3,15236,0),
                               (35,3,15255,0),
                               (36,3,15250,0),
                               (37,3,15260,0),
                               (38,3,15330,0),
                               (39,3,15345,0),
                               (40,3,15360,0),
                               (41,3,27398,0),
                               (42,3,15232,0);
INSERT INTO "user_subject_stats" VALUES
                                     (1,1,15030,0,0,0),
                                     (2,1,15050,2,1,1),
                                     (3,1,15080,0,0,0),
                                     (4,1,15120,0,0,0),
                                     (5,1,15210,0,0,0),
                                     (6,1,15236,0,0,0),
                                     (7,1,15255,0,0,0),
                                     (8,1,15250,0,0,0),
                                     (9,1,15260,0,0,0),
                                     (10,1,15330,0,0,0),
                                     (11,1,15345,0,0,0),
                                     (12,1,15360,0,0,0),
                                     (13,1,27398,0,0,0),
                                     (14,1,15232,0,0,0),
                                     (15,2,15030,0,0,0),
                                     (16,2,15050,1,1,1),
                                     (17,2,15080,0,0,0),
                                     (18,2,15120,0,0,0),
                                     (19,2,15210,0,0,0),
                                     (20,2,15236,0,0,0),
                                     (21,2,15255,0,0,0),
                                     (22,2,15250,0,0,0),
                                     (23,2,15260,0,0,0),
                                     (24,2,15330,0,0,0),
                                     (25,2,15345,0,0,0),
                                     (26,2,15360,0,0,0),
                                     (27,2,27398,0,0,0),
                                     (28,2,15232,0,0,0),
                                     (29,3,15030,0,0,0),
                                     (30,3,15050,0,0,0),
                                     (31,3,15080,0,0,0),
                                     (32,3,15120,0,0,0),
                                     (33,3,15210,0,0,0),
                                     (34,3,15236,0,0,0),
                                     (35,3,15255,0,0,0),
                                     (36,3,15250,0,0,0),
                                     (37,3,15260,0,0,0),
                                     (38,3,15330,0,0,0),
                                     (39,3,15345,0,0,0),
                                     (40,3,15360,0,0,0),
                                     (41,3,27398,0,0,0),
                                     (42,3,15232,0,0,0);
INSERT INTO "user_stats" VALUES
                             (1,1,0,1,2),
                             (2,2,1,1,1),
                             (3,3,0,0,0);
INSERT INTO "attempt" VALUES
                          (1,2,9448,'1',1,'2023-09-29 00:04:27'),
                          (2,2,9448,'0',0,'2023-09-29 00:05:55'),
                          (3,1,9449,'0',1,'2023-09-29 00:12:33'),
                          (4,1,9448,'1',1,'2023-09-29 00:13:08'),
                          (5,3,9448,'1',1,'2023-09-29 00:13:08');
INSERT INTO "comment" VALUES
                          (1,1,9448,'I got this wrong :(',0,0,0,'2023-09-29 00:21:09'),
                          (2,2,9450,'Do this, do that, then that, and you got the answer',0,1,2,'2023-09-29 01:33:40');
INSERT INTO "comment_vote" VALUES
                               (1,1,2,1,'2023-09-29 01:34:00'),
                               (2,2,2,1,'2023-09-29 01:34:32');
INSERT INTO "badge" VALUES
                        (1, 'badge 1', '/api/temp/sample/badge1.png', 15030, 'badge 1'),
                        (2, 'badge 2', '/api/temp/sample/badge2.png', 15050, 'badge 2'),
                        (3, 'badge 3', '/api/temp/sample/badge3.png', 15080, 'badge 3'),
                        (4, 'badge 4', '/api/temp/sample/badge4.png', 15120, 'badge 4'),
                        (5, 'badge 5', '/api/temp/sample/badge5.png', 15030, 'badge 5');
INSERT INTO "user_badge" VALUES
                             (1, 1, 1, 1),
                             (2, 1, 2, 0),
                             (3, 1, 3, 0),
                             (4, 2, 4, 1),
                             (5, 3, 5, 0),
                             (6, 1, 4, 0);





COMMIT;
PRAGMA ignore_check_constraints = ON;
PRAGMA foreign_keys = ON;
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;
