create table cliente(
nombre varchar(100),
direccion varchar(100),
correo varchar(100),
dpi varchar(100),
telefono varchar (100),
nit_cliente varchar(100) PRIMARY KEY
)

create table cuenta(
id_cuenta int IDENTITY (1,1) PRIMARY KEY,
tipo_de_cuenta varchar (100),
saldo float,
nit_cliente varchar(100)
constraint fk_nit_cliente foreign key (nit_cliente) references cliente(nit_cliente)
)

create table tipo_de_cuenta(
id_tipo int IDENTITY (1,1) PRIMARY KEY,
descripcion varchar (100)
)

create table detalle_transaccion(
id_transaccion int IDENTITY (1,1) PRIMARY KEY,
id_tipo int,
monto float,
constraint fk_id_tipo foreign key (id_tipo) references tipo_de_cuenta(id_tipo)
)

create table empleado(
id_empleado int IDENTITY(1,1) PRIMARY KEY,
nombre varchar(100),
puesto varchar(100),
correo varchar (100),
telefono varchar (100)
)

create table sucursal(
id_sucursal int IDENTITY (1,1) PRIMARY KEY,
nombre varchar (100),
direccion varchar (100),
telefono varchar (100),
horario varchar (100)
)

create table transaccion (
id_transaccion int,
fecha varchar (100),
hora varchar (100),
id_empleado int,
id_cuenta int,
id_sucursal int
constraint fk_id_transaccion foreign key (id_transaccion) references detalle_transaccion(id_transaccion),
constraint fk_id_empleado foreign key (id_empleado) references empleado (id_empleado),
constraint fk_id_cuenta foreign key (id_cuenta) references cuenta (id_cuenta),
constraint fk_id_sucursal foreign key (id_sucursal) references sucursal(id_sucursal)
)