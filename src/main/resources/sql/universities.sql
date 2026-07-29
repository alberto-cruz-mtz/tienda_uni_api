INSERT INTO universities(name, acronym, brand_color, logo_url, state, city)
VALUES ('Universidad Tecnológica del Sureste de Veracruz', 'UTSV', '#1A6B35',
        'https://lh5.googleusercontent.com/proxy/TXbH0sLBa0j0d7TQNP7QO8l_Sca2u5P4SwiVzrxGCtbvSW5oAd9Y20D6LHeu0fwTKO92A_33zbpt3GJlI4V-EA',
        'Veracruz', 'Nanchital'),
       ('Universidad Nacional Autónoma de Mexico', 'UNAM', '#EFBF04',
        'https://francia.unam.mx/wp-content/uploads/2021/10/cropped-Logo-UNAM-Dorado-Square.png',
        'Ciudad de Mexico', 'Coyoacan'),
       ('Instituto Politécnico Nacional', 'IPN', '#952f57',
        'https://sociedadtecnologiaydeontologia.wordpress.com/wp-content/uploads/2019/01/logotipo_ipn.png',
        'Ciudad de Mexico', 'Ciudad de Mexico');

INSERT INTO university_domains(university_id, domain)
VALUES ((SELECT id FROM universities WHERE acronym = 'UTSV'), 'alumnos.utsv.edu.mx'),
       ((SELECT id FROM universities WHERE acronym = 'UTSV'), 'administrativos.utsv.edu.mx'),
       ((SELECT id FROM universities WHERE acronym = 'UTSV'), 'docentes.utsv.edu.mx'),
       ((SELECT id FROM universities WHERE acronym = 'UNAM'), 'alumnos.unam.mx'),
       ((SELECT id FROM universities WHERE acronym = 'UNAM'), 'administrativos.unam.mx'),
       ((SELECT id FROM universities WHERE acronym = 'UNAM'), 'docentes.unam.mx'),
       ((SELECT id FROM universities WHERE acronym = 'IPN'), 'alumnos.ipn.edu.mx'),
       ((SELECT id FROM universities WHERE acronym = 'IPN'), 'administrativos.ipn.edu.mx'),
       ((SELECT id FROM universities WHERE acronym = 'IPN'), 'docentes.ipn.edu.mx');
