-- import to SQLite by running: sqlite3.exe db.sqlite3 -init sqlite.sql

PRAGMA journal_mode = MEMORY;
PRAGMA synchronous = OFF;
PRAGMA foreign_keys = OFF;
PRAGMA ignore_check_constraints = OFF;
PRAGMA auto_vacuum = NONE;
PRAGMA secure_delete = OFF;
BEGIN TRANSACTION;

DROP TABLE IF EXISTS `pending_email_update`;
DROP TABLE IF EXISTS `pending_password_reset`;
DROP TABLE IF EXISTS `user_subject_stats`;
DROP TABLE IF EXISTS `user_subject`;
DROP TABLE IF EXISTS `user_stats`;
DROP TABLE IF EXISTS `user_role`;
DROP TABLE IF EXISTS `user_detail`;
DROP TABLE IF EXISTS `user_badge`;
DROP TABLE IF EXISTS `user_auth`;
DROP TABLE IF EXISTS `pending_user`;
DROP TABLE IF EXISTS `attempt`;
DROP TABLE IF EXISTS `comment_vote`;
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `question`;

CREATE TABLE `badge` (
                         `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                         `name` TEXT NOT NULL,
                         `icon` text NOT NULL ,
                         `subject` int NOT NULL ,
                         `description` text
                         
);

CREATE TABLE `institute` (
                             `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                             `name` TEXT NOT NULL,
                             `icon` text NOT NULL
                             
);

CREATE TABLE `pending_user` (
                                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                                `email` TEXT NOT NULL,
                                `digest` TEXT NOT NULL,
                                `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
                                
);

CREATE TABLE `user` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                        `display_name` char(64) NOT NULL,
                        `avatar` text NOT NULL ,
                        `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
                        
);

CREATE TABLE `user_auth` (
                             `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                             `user_id` int NOT NULL,
                             `email` TEXT NOT NULL,
                             `digest` TEXT NOT NULL
                             ,
                             FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `user_badge` (
                              `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                              `user_id` int NOT NULL,
                              `badge_id` int NOT NULL,
                              `is_displayed` tinyint NOT NULL DEFAULT '0'
                              ,
                              FOREIGN KEY (`badge_id`) REFERENCES `badge` (`id`),
                              FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `user_detail` (
                               `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                               `user_id` int NOT NULL,
                               `inst_id` int,
                               FOREIGN KEY (`inst_id`) REFERENCES `institute` (`id`),
                               FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `user_role` (
                             `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                             `user_id` int NOT NULL,
                             `role` tinyint NOT NULL,
                             FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `user_stats` (
                              `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                              `user_id` int NOT NULL,
                              `total_endorsed` int NOT NULL DEFAULT '0' ,
                              `total_attempted` int NOT NULL DEFAULT '0' ,
                              `total_correct` int NOT NULL DEFAULT '0' ,
                              FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `user_subject` (
                                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                                `user_id` int NOT NULL,
                                `subject` int NOT NULL,
                                `is_enabled` tinyint NOT NULL DEFAULT '0',
                                FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `user_subject_stats` (
                                      `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                                      `user_id` int NOT NULL,
                                      `subject` int NOT NULL,
                                      `total_attempted` int NOT NULL DEFAULT '0' ,
                                      `total_correct` int NOT NULL DEFAULT '0' ,
                                      `total_correct_first` int NOT NULL DEFAULT '0' ,
                                      FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `pending_password_reset` (
                                          `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                                          `user_id` int NOT NULL,
                                          `token` TEXT NOT NULL ,
                                          `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `pending_email_update` (
                                        `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                                        `user_id` int NOT NULL,
                                        `email` TEXT NOT NULL,
                                        `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `question` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                            `subject` int NOT NULL,
                            `topic` int NOT NULL,
                            `type` int NOT NULL,
                            `difficulty` int NOT NULL,
                            `body` text NOT NULL ,
                            `answer` text NOT NULL ,
                            `title` TEXT NOT NULL ,
                            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
                            
);

CREATE TABLE `attempt` (
                           `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                           `user_id` int NOT NULL,
                           `question_id` int NOT NULL,
                           `answer` text NOT NULL ,
                           `is_correct` tinyint NOT NULL,
                           `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (`question_id`) REFERENCES `question` (`id`),
                           FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `comment` (
                           `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                           `commenter_id` int NOT NULL ,
                           `question_id` int NOT NULL,
                           `content` text NOT NULL,
                           `content_type` tinyint NOT NULL ,
                           `is_endorsed` tinyint NOT NULL DEFAULT '0',
                           `total_votes` int NOT NULL DEFAULT '0' ,
                           `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (`question_id`) REFERENCES `question` (`id`),
                           FOREIGN KEY (`commenter_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `comment_vote` (
                                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                                `voter_id` int NOT NULL ,
                                `comment_id` int NOT NULL,
                                `direction` tinyint NOT NULL ,
                                `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`),
                                FOREIGN KEY (`voter_id`) REFERENCES `user` (`id`)
);



CREATE UNIQUE INDEX `user_auth_user_id_UNIQUE` ON `user_auth` (`user_id`);
CREATE UNIQUE INDEX `user_auth_email_UNIQUE` ON `user_auth` (`email`);
CREATE INDEX `user_badge_user_id_fk_idx` ON `user_badge` (`user_id`);
CREATE INDEX `user_badge_badge_id_fk_idx` ON `user_badge` (`badge_id`);
CREATE UNIQUE INDEX `user_detail_user_id_UNIQUE` ON `user_detail` (`user_id`);
CREATE INDEX `user_detail_user_id_fk_idx` ON `user_detail` (`user_id`);
CREATE INDEX `user_detail_inst_id_fk_idx` ON `user_detail` (`inst_id`);
CREATE UNIQUE INDEX `user_role_user_id_UNIQUE` ON `user_role` (`user_id`);
CREATE UNIQUE INDEX `user_stats_user_id_UNIQUE` ON `user_stats` (`user_id`);
CREATE INDEX `user_subject_user_subject_user_id_fk_idx` ON `user_subject` (`user_id`);
CREATE INDEX `user_subject_stats_user_subject_stats_user_id_fk_idx` ON `user_subject_stats` (`user_id`);
CREATE INDEX `pending_password_reset_pending_password_reset_user_id_fk_idx` ON `pending_password_reset` (`user_id`);
CREATE INDEX `pending_email_update_pending_email_update_user_id_fk_idx` ON `pending_email_update` (`user_id`);
CREATE INDEX `attempt_attempt_user_id_idx` ON `attempt` (`user_id`);
CREATE INDEX `attempt_attempt_question_id_fk_idx` ON `attempt` (`question_id`);
CREATE INDEX `comment_comment_user_id_fk_idx` ON `comment` (`commenter_id`);
CREATE INDEX `comment_comment_question_id_fk_idx` ON `comment` (`question_id`);
CREATE INDEX `comment_vote_comment_vote_user_id_fk_idx` ON `comment_vote` (`voter_id`);
CREATE INDEX `comment_vote_comment_vote_comment_id_fk_idx` ON `comment_vote` (`comment_id`);

COMMIT;
PRAGMA ignore_check_constraints = ON;
PRAGMA foreign_keys = ON;
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;
