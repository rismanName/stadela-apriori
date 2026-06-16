# Stadela Coffee — Desktop App (Java Swing)

## Cara Compile & Jalankan

### Prasyarat
- Java 11+
- Maven 3.6+
- MySQL / MariaDB dengan database `db_stadela`

### 1. Import database
```
mysql -u root -p < ../db_stadela.sql
```

### 2. Sesuaikan koneksi DB
Edit file: `src/main/java/com/stadela/db/DBConnection.java`
```java
private static final String HOST   = "127.0.0.1";
private static final String DBNAME = "db_stadela";
private static final String USER   = "root";
private static final String PASS   = "password_anda";
```

### 3. Buka di NetBeans
File → Open Project → pilih folder `stadela-desktop`
(NetBeans otomatis detect sebagai Maven project)

### 4. Compile & Run
Klik **Run Project** (F6) atau:
```bash
mvn package
java -jar target/stadela-desktop-1.0.0.jar
```

## Login default
- Username: `admin`
- Password: `admin123` (sesuai hash di db_stadela.sql)

## Struktur Project
```
src/main/java/com/stadela/
├── Main.java               # Entry point
├── db/
│   └── DBConnection.java   # Koneksi MySQL
├── model/                  # Entity classes
├── dao/                    # Database access (CRUD)
├── ui/
│   ├── LoginFrame.java
│   ├── MainFrame.java      # Window utama + sidebar
│   └── panel/              # Setiap halaman = 1 panel
└── util/
    ├── AprioriEngine.java  # Algoritma Apriori
    └── SessionManager.java # Manajemen sesi login
```
