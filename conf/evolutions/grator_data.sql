

insert  into `grator_app`(`id`,`name`,`path`) values (1,'grator','/home/pablo/projects/grator/');


/*Data for the table `grator_module` */

insert  into `grator_module`(`id`,`name`,`application_id`,`has_tab`)
  values
  (1,'gratorApp',1,1),
  (2,'gratorModule',1,1),
  (3,'gratorField',1,1),
  (4,'gratorRelationship',1,1),
  (5,'gratorFieldType',1,0),
  (6,'gratorSubpanel',1,1);

  /*Data for the table `grator_field` */

  insert  into `grator_field`(`id`,`name`,`module_id`,`field_type`,`required`,`related_module_id`)
    values
    (1,'id',1,1,1,1),
    (2,'name',1,2,1,1),
    (3,'path',1,3,1,1),
    (4,'id',2,1,1,2),
    (5,'name',2,2,1,2),
    (6,'applicationId',2,6,1,1),
    (7,'id',3,1,1,3),
    (8,'name',3,2,1,3),
    (9,'moduleId',3,6,1,2),
    (10,'fieldType',3,6,1,1),
    (11,'required',3,5,0,1),
    (12,'relatedModuleId',3,6,0,2),
    (13,'id',4,1,1,4),
    (14,'name',4,2,1,4),
    (15,'relType',4,3,1,1),
    (16,'primaryModuleId',4,6,1,2),
    (17,'primaryModuleLabel',4,3,1,1),
    (18,'primaryModuleSubpanel',4,3,1,1),
    (19,'relatedModuleId',4,6,1,2),
    (20,'relatedModuleLabel',4,3,1,1),
    (21,'relatedModuleSubpanel',4,3,1,1),
    (22,'id',5,1,1,2),
    (23,'name',5,2,1,2),
    (24, 'id',6,1,1,6),
    (25, 'name',6,2,1,6),
    (26, 'toModule',6,6,1,2),
    (27, 'fromModule',6,6,1,2),
    (28, 'fromField',6,6,1,3);

insert  into `grator_subpanel`(`id`,`name`,`to_module`,`from_module`,`from_field`) values (1,'modules',1,2,6);
