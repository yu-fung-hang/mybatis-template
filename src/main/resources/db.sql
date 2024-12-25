CREATE TABLE `demo_user` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `create_time` datetime NOT NULL,
                             `email` varchar(80) COLLATE utf8mb4_bin NOT NULL,
                             `password` varchar(256) COLLATE utf8mb4_bin NOT NULL,
                             `status` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                             `ts` datetime NOT NULL,
                             `username` varchar(30) COLLATE utf8mb4_bin NOT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `UK_ew460ftlhbmyh06f49626xrst` (`email`),
                             UNIQUE KEY `UK_qebvxk10d08yfjdexgplbb8tu` (`username`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;