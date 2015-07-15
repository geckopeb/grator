# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `grator_app` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`path` VARCHAR(254) NOT NULL);
create table `grator_field` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`module_id` BIGINT NOT NULL,`field_type` BIGINT NOT NULL,`required` BOOLEAN NOT NULL,`related_module_id` BIGINT);
create table `grator_module` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`application_id` BIGINT NOT NULL, `has_tab` BOOLEAN NOT NULL);
create table `grator_relationship` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`rel_type` VARCHAR(254) NOT NULL,`primary_module_id` BIGINT NOT NULL,`primary_module_label` VARCHAR(254) NOT NULL,`primary_module_subpanel` VARCHAR(254) NOT NULL,`related_module_id` BIGINT NOT NULL,`related_module_label` VARCHAR(254) NOT NULL,`related_module_subpanel` VARCHAR(254) NOT NULL);
create table `grator_field_type` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL);
create table `grator_subpanel` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`to_module` BIGINT NOT NULL,`from_module` BIGINT NOT NULL,`from_field` BIGINT NOT NULL);

alter table `grator_field` add constraint `grator_field_grator_module_module_id` foreign key(`module_id`) references `grator_module`(`id`) on update NO ACTION on delete NO ACTION;
alter table `grator_field` add constraint `grator_field_grator_module_related_module_id` foreign key(`related_module_id`) references `grator_module`(`id`) on update NO ACTION on delete NO ACTION;
alter table `grator_module` add constraint `grator_module_grator_app_application_id` foreign key(`application_id`) references `grator_app`(`id`) on update NO ACTION on delete NO ACTION;
alter table `grator_relationship` add constraint `grator_relationship_grator_module_primary_module_id` foreign key(`primary_module_id`) references `grator_module`(`id`) on update NO ACTION on delete NO ACTION;
alter table `grator_relationship` add constraint `grator_relationship_grator_module_related_module_id` foreign key(`related_module_id`) references `grator_module`(`id`) on update NO ACTION on delete NO ACTION;
alter table `grator_field` add constraint `grator_field_grator_field_type_field_type` foreign key(`field_type`) references `grator_field_type`(`id`) on update NO ACTION on delete NO ACTION;
alter table `grator_subpanel` add constraint `grator_subpanel_grator_module_to_module` foreign key(`to_module`) references `grator_module`(`id`) on update NO ACTION on delete NO ACTION;
alter table `grator_subpanel` add constraint `grator_subpanel_grator_module_from_module` foreign key(`from_module`) references `grator_module`(`id`) on update NO ACTION on delete NO ACTION;
alter table `grator_subpanel` add constraint `grator_subpanel_grator_field_from_field` foreign key(`from_field`) references `grator_field`(`id`) on update NO ACTION on delete NO ACTION;

INSERT INTO grator_field_type(id, name)
VALUES
(1, "Id"),
(2, "Name"),
(3, "Text"),
(4, "Integer"),
(5, "Boolean"),
(6, "RelatedDropdown"),
(7, "RelatedCombo");

# --- !Downs

ALTER TABLE grator_relationship DROP FOREIGN KEY grator_relationship_grator_module_primary_module_id;
ALTER TABLE grator_relationship DROP FOREIGN KEY grator_relationship_grator_module_related_module_id;
ALTER TABLE grator_module DROP FOREIGN KEY grator_module_grator_app_application_id;
ALTER TABLE grator_field DROP FOREIGN KEY grator_field_grator_module_module_id;
ALTER TABLE grator_field DROP FOREIGN KEY grator_field_grator_module_related_module_id;
ALTER TABLE grator_field DROP FOREIGN KEY grator_field_grator_field_type;
drop table `grator_field_type`;
drop table `grator_relationship`;
drop table `grator_module`;
drop table `grator_field`;
drop table `grator_app`;
drop table `grator_subpanel`;
