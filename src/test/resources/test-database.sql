DROP TABLE IF EXISTS custom_app_container;
CREATE TABLE custom_app_container
(
   id INTEGER AUTO_INCREMENT PRIMARY KEY,
   create_date TIMESTAMP DEFAULT now(),
   modify_date TIMESTAMP DEFAULT now(),
   custom_app_icon_id INTEGER NOT NULL,
   name VARCHAR(100) NOT NULL,
   sequence_no INTEGER NOT NULL,
   permission_id INTEGER,
   place_before_container VARCHAR(100)
);

DROP TABLE IF EXISTS custom_app_section;
CREATE TABLE custom_app_section
(
   id INTEGER AUTO_INCREMENT PRIMARY KEY,
   create_date TIMESTAMP DEFAULT now(),
   modify_date TIMESTAMP DEFAULT now(),
   custom_app_container_id INTEGER NOT NULL,
   name VARCHAR(100) NOT NULL,
   sequence_no INTEGER NOT NULL
);

DROP TABLE IF EXISTS custom_app;
CREATE TABLE custom_app
(
   id INTEGER AUTO_INCREMENT PRIMARY KEY,
   create_date TIMESTAMP DEFAULT now(),
   modify_date TIMESTAMP DEFAULT now(),
   custom_app_section_id INTEGER NOT NULL,
   custom_app_backend_config_id INTEGER NOT NULL,
   name VARCHAR(100) NOT NULL,
   custom_app_icon_id VARCHAR(100) NOT NULL,
   sequence_no INTEGER NOT NULL,
   looker_dashboard_id INTEGER,
   permission_id INTEGER
);

DROP TABLE IF EXISTS custom_app_backend_config;
CREATE TABLE custom_app_backend_config
(
   id INTEGER AUTO_INCREMENT PRIMARY KEY,
   create_date TIMESTAMP DEFAULT now(),
   modify_date TIMESTAMP DEFAULT now(),
   custom_app_backend_id INTEGER NOT NULL,
   name VARCHAR(100) NOT NULL
);

DROP TABLE IF EXISTS custom_app_icon;
CREATE TABLE custom_app_icon
(
   id INTEGER AUTO_INCREMENT PRIMARY KEY,
   create_date TIMESTAMP DEFAULT now(),
   modify_date TIMESTAMP DEFAULT now(),
   icon_id VARCHAR(100) NOT NULL,
   name VARCHAR(100) NOT NULL
);

