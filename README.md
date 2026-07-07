# Sistema de Alquiler de Películas - Sakila

## Descripción

Sistema web desarrollado con Spring Boot y PostgreSQL para la gestión de alquiler de películas utilizando la base de datos Sakila (Pagila). La aplicación permite administrar películas, categorías, inventario y alquileres mediante una API REST documentada con Swagger/OpenAPI.

El sistema implementa autenticación con Spring Security, control de acceso basado en roles (ADMIN/USER), gestión transaccional de rentas múltiples con control de concurrencia mediante bloqueos pesimistas, y cálculo dinámico de disponibilidad de inventario.

## Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.3.0**
- **Spring Security** - Autenticación HTTP Basic con BCrypt
- **Spring Data JPA** - Persistencia con Hibernate
- **PostgreSQL** - Base de datos relacional (Pagila schema)
- **Maven** - Gestión de dependencias y construcción
- **Swagger/OpenAPI** (SpringDoc) - Documentación interactiva de la API
- **HikariCP** - Pool de conexiones a base de datos
- **Lombok** - Reducción de código boilerplate

## Requisitos

- **JDK 17** o superior
- **PostgreSQL 12** o superior
- **Maven 3.6** o superior
- **pgAdmin 4** (opcional, para administración de BD)

## Configuración

### 1. Crear la base de datos Sakila

Ejecutar en PostgreSQL como superusuario (`postgres`):

```sql
CREATE DATABASE sakila;
CREATE USER sakila_user WITH PASSWORD 'admin123';
GRANT ALL PRIVILEGES ON DATABASE sakila TO sakila_user;
```

### 2. Importar el esquema Pagila

Descargar e importar el dump de Pagila desde:
- https://github.com/devrimgunduz/pagila

O ejecutar los scripts SQL incluidos en la carpeta `sql/`:

```bash
psql -U postgres -d sakila -f sql/pagila-schema.sql
psql -U postgres -d sakila -f sql/sakila-custom-tables.sql
psql -U postgres -d sakila -f sql/grant-permissions.sql
```

### 3. Configurar application.properties

Las credenciales de base de datos están en `src/main/resources/application.properties` con valores por defecto para desarrollo local:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sakila
spring.datasource.username=sakila_user
spring.datasource.password=admin123
```

### 4. Variables de entorno (opcional)

Para sobreescribir los valores por defecto, define estas variables de entorno:

```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sakila
DB_USERNAME=sakila_user
DB_PASSWORD=admin123
APP_ADMIN_USERNAME=admin
APP_ADMIN_PASSWORD=admin123
```

## Ejecución

### Compilar y empaquetar

```bash
mvn clean package -DskipTests
```

Esto generará el archivo WAR en `target/Yugcha_Romel_Sakila.war`

### Ejecutar con Maven

```bash
mvn spring-boot:run
```

### Ejecutar el WAR generado

```bash
java -jar target/Yugcha_Romel_Sakila.war
```

### Desplegar en Tomcat

Copiar `target/Yugcha_Romel_Sakila.war` a la carpeta `webapps` de Apache Tomcat.

La aplicación estará disponible en: **http://localhost:8080**

## Swagger / Documentación de la API

Una vez iniciada la aplicación, acceder a la documentación interactiva en:

**http://localhost:8080/swagger-ui.html**

Desde Swagger UI:
1. Hacer clic en **Authorize**
2. Ingresar credenciales:
   - **Username:** `admin`
   - **Password:** `admin123`
3. Probar los endpoints directamente desde el navegador

**Documentación JSON:**
http://localhost:8080/v3/api-docs

## Funcionalidades

### Autenticación y Autorización
- **POST /api/auth/register** - Registro de nuevos usuarios (rol USER)
- **POST /api/auth/login** - Inicio de sesión con HTTP Basic Auth
- Control de acceso basado en roles (`ADMIN`, `USER`)
- Encriptación de contraseñas con BCrypt

### CRUD Categorías (ADMIN)
- **GET /api/admin/categories** - Listar todas las categorías
- **GET /api/admin/categories/{id}** - Obtener categoría por ID
- **POST /api/admin/categories** - Crear nueva categoría
- **PUT /api/admin/categories/{id}** - Actualizar categoría
- **DELETE /api/admin/categories/{id}** - Eliminar categoría

### CRUD Películas (ADMIN)
- **GET /api/films** - Listar todas las películas con disponibilidad (público)
- **GET /api/films/{id}** - Ver detalle de película (público)
- **POST /api/admin/films** - Crear película con inventario inicial
- **PUT /api/admin/films/{id}** - Actualizar película
- **DELETE /api/admin/films/{id}** - Eliminar película

### Gestión de Inventario (ADMIN)
- **POST /api/admin/films/{id}/inventory?quantity=N** - Agregar unidades de inventario
- Cálculo dinámico de stock disponible (total - alquileres activos)

### Alquiler de Películas (USER)
- **POST /api/rentals** - Rentar una o varias películas (transaccional)
- **GET /api/rentals/active** - Ver mis alquileres activos
- **GET /api/rentals/history** - Ver historial completo de alquileres
- **PATCH /api/rentals/{id}/return** - Devolver película

### Consultas Administrativas (ADMIN)
- **GET /api/admin/rentals** - Ver todos los alquileres del sistema
- **GET /api/admin/rentals/active** - Ver todos los alquileres activos

### Reglas de Negocio Implementadas
- ✅ Stock dinámico calculado en tiempo real
- ✅ Control de concurrencia con bloqueo pesimista (`PESSIMISTIC_WRITE`)
- ✅ Rentas múltiples transaccionales con rollback completo en caso de error
- ✅ Solo el propietario del rental puede devolver una película
- ✅ Validación de disponibilidad antes de confirmar renta
- ✅ Validación de duplicados en usernames y nombres de categorías

## Estructura del Proyecto

```
src/main/java/com/espe/sakila
├── controller          # Endpoints REST (CategoryController, FilmController, RentalController, AuthController)
├── service             # Lógica de negocio (CategoryService, FilmService, RentalService, AuthService)
├── repository          # Interfaces JPA (CategoryRepository, FilmRepository, InventoryRepository, RentalRepository, UserRepository, RoleRepository)
├── entity              # Entidades JPA (Category, Film, Inventory, Rental, User, Role)
├── dto                 # Data Transfer Objects (CategoryDTO, FilmDTO, RentalRequestDTO, RentalResponseDTO, UserDTO)
├── mapper              # Conversores Entity ↔ DTO (CategoryMapper, FilmMapper, RentalMapper)
├── security            # Configuración de seguridad (SecurityConfig, UserDetailsServiceImpl, DataInitializer)
├── config              # Configuración adicional (SwaggerConfig)
└── exception           # Manejo de errores (GlobalExceptionHandler, ResourceNotFoundException, DuplicateEntityException, InsufficientStockException, InvalidOperationException)
```

## Arquitectura

El proyecto sigue una **arquitectura en capas**:

1. **Capa de Presentación (Controller):** Expone los endpoints REST y maneja las peticiones HTTP
2. **Capa de Negocio (Service):** Contiene la lógica de negocio y reglas transaccionales
3. **Capa de Persistencia (Repository):** Acceso a datos mediante Spring Data JPA
4. **Capa de Entidades (Entity/DTO/Mapper):** Modelo de dominio y transferencia de datos
5. **Capa de Seguridad (Security):** Autenticación, autorización y configuración de Spring Security
6. **Capa de Manejo de Errores (Exception):** Control centralizado de excepciones con respuestas HTTP consistentes

## Credenciales por Defecto

Al iniciar la aplicación por primera vez, se crea automáticamente un usuario administrador:

- **Username:** `admin`
- **Password:** `admin123`

Los usuarios registrados mediante `/api/auth/register` obtienen automáticamente el rol `USER`.

## Notas de Implementación

- **Packaging:** WAR compatible con servidores de aplicaciones (Tomcat, Wildfly) y ejecución standalone
- **Pool de conexiones:** HikariCP configurado con 10 conexiones máximas
- **Transaccionalidad:** Todas las operaciones de escritura están envueltas en `@Transactional`
- **Lazy Loading:** Las relaciones JPA están optimizadas con `FetchType.EAGER` en puntos críticos (User → Role)
- **Control de Concurrencia:** Bloqueo pesimista en consultas de inventario disponible para evitar double booking
- **Validación:** Bean Validation (Jakarta) en todos los DTOs de entrada
- **Manejo de Errores:** GlobalExceptionHandler con respuestas JSON estructuradas y sin exposición de stack traces

## Autor

**Romel Yugcha**  
UNIVERSIDAD DE LAS FUERZAS ARMADAS (ESPE)  
2026
