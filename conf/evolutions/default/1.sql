# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `application` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`path` VARCHAR(254) NOT NULL);
create table `field` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`module_id` BIGINT NOT NULL,`field_type` VARCHAR(254) NOT NULL,`required` BOOLEAN NOT NULL,`related_module_id` BIGINT);
create table `module` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`application_id` BIGINT NOT NULL);
alter table `field` add constraint `field_related_module_id` foreign key(`related_module_id`) references `module`(`id`) on update NO ACTION on delete NO ACTION;
alter table `field` add constraint `field_module_id` foreign key(`module_id`) references `module`(`id`) on update NO ACTION on delete NO ACTION;
alter table `module` add constraint `module_application_id` foreign key(`application_id`) references `application`(`id`) on update NO ACTION on delete NO ACTION;

# --- !Downs

ALTER TABLE field DROP FOREIGN KEY field_related_module_id;
ALTER TABLE field DROP FOREIGN KEY field_module_id;
ALTER TABLE module DROP FOREIGN KEY module_application_id;
drop table `application`;
drop table `field`;
drop table `module`;

