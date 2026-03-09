Neal Guarddin\
Pemrograman Lanjut A\
2406348282

# Link Website:
[Website Heroku Eshop Neal](https://boiling-cliffs-22997-639f62fcff8d.herokuapp.com/product/list)

# Refleksi 1 (Coding Standards):
Refleksi proses coding selama exercise 1:
1. Sempat ketika run application tidak ditemukan file htmlnya (CreateProduct.html dan ProductList.html), ternyata cuman salah format nama hehe
2. Implementasi clean code yang maksudnya mudah dibaca, kuat untuk dipahami, dan mudah dijaga. Konvensi nama variabel, 
nama fungsi, error handler, dan class organizations. Menurut prinsip Clean Code, fungsi bisa jadi pendek, bagus dinamai, dan rapih.
3. Mengurangi penggunaan comment code, sehingga setiap konvensi nama variabel dan method harus jelas
4. Untuk pembuatan fitur edit dan delete product menggunakan `productId` bertipe String. Awalnya mengerjakan untuk branch 
edit-product baru setelah itu delete-product. Untuk edit-product menggunakan method `updateProduct()`. Sementara delete-product
menggunakan methid `deleteProduct()`. Untuk halaman edit dibuat file html baru yaiut `EditProduct.html`
5. Terjadi kesalahan dalam git stash dan cara mergenya. Akhirnya belajar gimana cara pakai `git stash` dan `git stash pop`
dengan benar dan kapan penggunaannya ketika pindah branch. Dan apabila terjadi kesalahan dalam `git stash`, gimana cara retrieve
data berdasarkan `git stash list`

# Refleksi 2 (Coding Standards):
Refleksi proses coding selama exercise 2:
1. Setelah menulis unit test rasanya lebih kalem karena perubahan kecil di kode lebih gampang kedeteksi.
Namun, dalam penulisan unit test juga harus teliti. Harus jelas input, proses, dan output yang diharapkan.\
**Berapa banyak unit test dalam satu class?**\
Tidak ada angka baku. Tapi yang lebih penting:
   - Tiap method dites keadaan positif dan negatif
   - Tiap method/tes fokus pada satu skenario
   - Test harus mudah dibaca dan tidak saling bergantung

    **Bagaimana memastikan unit test “cukup”?**\
    Metrik paling membantu adalah *code coverage*(statement/branch coverage). Coverage membantu melihat bagian kode mana
   yang belum tersentuh test. Tapi coverage bukan tujuan utama. **Tujuan utama** adalah memverifikasi perilaku yang penting dan rawan error.

    **Kalau coverage 100%, apakah berarti tidak ada bug?**  
**Tidak**. 100% coverage tidak menjamin bebas bug, karena:
   - Test bisa “melewati” baris kode tanpa assertion yang kuat.
   - Edge case atau kombinasi input tertentu mungkin belum dites.
   - Bug bisa muncul di integrasi antar komponen, konfigurasi, atau concurrency.
   - Requirement bisa salah dipahami. Test jadi memverifikasi hal yang keliru.

2. Refleksi kebersihan kode saat membuat functional test baru\
   Jika saya diminta membuat functional test suite baru untuk memverifikasi
   jumlah item di product list, lalu saya menyalin setup yang sama seperti suite
   sebelumnya, maka kode **berpotensi kurang bersih karena duplikasi**.
**Potensi isu Clean Code:**
   - **Duplikasi (melanggar DRY):**
     setup `baseUrl`, `WebDriverWait`, dan navigasi halaman diulang.
   - **Magic strings & selector tersebar:**
     path seperti `/product/create` dan locator seperti `By.id("nameInput")`
     muncul di banyak tempat (redundan), sehingga rapuh saat UI berubah.
   - **Tight coupling ke detail UI:**
     perubahan kecil di HTML dapat mematahkan banyak test.
   - **Flaky test risk:**
     functional test bergantung timing/redirect; wait yang tidak konsisten
     bisa membuat test tidak stabil.
   - **Readability menurun:**
     boilerplate Selenium membuat intent test kurang terlihat.

    **Saran perbaikan:**
   - Ekstrak common setup ke helper atau base class (mis. `BaseFunctionalTest`).
   - Terapkan **Page Object Model (POM)**:
     buat `CreateProductPage` dan `ProductListPage` yang menyimpan locator dan aksi.
   - Gunakan konstanta untuk URL dan locator agar tidak tersebar (konsisten di semua pemakaian).
   - Buat helper reusable untuk membuat product (seed),
     agar test lain fokus pada assertion.
   - Jaga isolasi antar test:
     pastikan state data tidak bocor antar test (reset data bila perlu).

    Berdasarkan cara ini, harapannya test suite baru tetap ringkas, mudah dirawat,
dan lebih tahan terhadap perubahan UI.

# Refleksi 3 (CI/CD & Code Quality)

## 1) Code quality issue yang saya perbaiki + strategi saya
Selama exercise ini saya fokus beresin temuan dari static analysis (PMD) yang sifatnya kecil tapi beriis, supaya pipeline bersih dan kode lebih rapi.

Beberapa isu yang saya perbaiki:
- **Modifier `public` yang tidak perlu di interface** (Java interface method sudah otomatis `public`).  
  Saya hapus modifier yang redundant supaya nggak memunculkan warning dan biar konsisten dengan konvensi Java. (Bisa dilihat [src/main/java/id/ac/ui/cs/advprog/eshop/service/ProductService.java](src/main/java/id/ac/ui/cs/advprog/eshop/service/ProductService.java))
- **Import yang tidak dipakai / wildcard import** di controller.  
  Saya rapikan import dan buang yang tidak kepakai karena bikin warning dan bikin file terlihat “berantakan”. (Bisa dilihat [src/main/java/id/ac/ui/cs/advprog/eshop/controller/ProductController.java](src/main/java/id/ac/ui/cs/advprog/eshop/controller/ProductController.java))
- **Parameter yang tidak dipakai** (contoh: ada parameter method yang sebenarnya tidak digunakan).  
  Saya hapus supaya signature lebih bersih dan tidak misleading.

Strategi saya waktu fixing:
1) Lihat list warning/annotation dari workflow quality gate dulu.  
2) Milih yang paling aman (tanpa mengubah behaviour) untuk dikerjakan lebih dulu.  
3) Setiap perubahan saya commit kecil-kecil, lalu push untuk mastiin github workflow tetap jalan dan warning-nya benar-benar hilang.

## 2) Apakah workflow saya sudah memenuhi CI dan CD?
Menurut saya, workflow ini sudah memenuhi **Continuous Integration** karena setiap ada push/PR, GitHub Actions menjalankan proses otomatis untuk cek kualitas kode lewat workflow [PMD](.github/workflows/pmd.yml). Dengan begitu, masalah bisa ketahuan lebih cepat sebelum perubahan masuk ke branch utama.

Untuk **Continuous Deployment**, saya pakai **Heroku GitHub Automatic Deploy** (buildpack), jadi setiap ada perubahan yang masuk ke branch `main/master` di GitHub, Heroku akan otomatis build dan release versi terbaru. Karena deploy-nya via buildpack (bukan container), aplikasi dijalankan memakai command dari [Procfile](Procfile), dan versi Java dipin lewat [system.properties](system.properties).

Catatan: workflow [Deploy to Heroku (Docker)](.github/workflows/deploy-heroku.yml) saya nonaktifkan (manual saja) karena deployment-nya sudah ditangani langsung oleh Heroku GitHub integration, jadi tidak dobel jalur deploy.

# Refleksi 4 (SOLID)

## 1) Prinsip SOLID apa yang saya terapkan di project ini?

### **SRP (Single Responsibility Principle)**
Disini saya memisahkan tanggung jawab tiap layer:
- **Controller** hanya mengurus HTTP request/response dan view: [`id.ac.ui.cs.advprog.eshop.controller.ProductController`](src/main/java/id/ac/ui/cs/advprog/eshop/controller/ProductController.java)
- **Service** mengurus logika aplikasi dan menjadi penghubung controller-repository: [`id.ac.ui.cs.advprog.eshop.service.ProductServiceImpl`](src/main/java/id/ac/ui/cs/advprog/eshop/service/ProductServiceImpl.java)
- **Repository** mengurus penyimpanan data (in-memory): [`id.ac.ui.cs.advprog.eshop.repository.InMemoryProductRepository`](src/main/java/id/ac/ui/cs/advprog/eshop/repository/InMemoryProductRepository.java)
- **Id generation** dipisah ke abstraction khusus: [`id.ac.ui.cs.advprog.eshop.util.IdGenerator`](src/main/java/id/ac/ui/cs/advprog/eshop/util/IdGenerator.java) dan implementasinya [`id.ac.ui.cs.advprog.eshop.util.UuidIdGenerator`](src/main/java/id/ac/ui/cs/advprog/eshop/util/UuidIdGenerator.java)

### **OCP (Open/Closed Principle)**
Beberapa bagian dibuat *extendable* tanpa mengubah kode lama:
- Repository dibuat berbasis interface: [`id.ac.ui.cs.advprog.eshop.repository.ProductRepository`](src/main/java/id/ac/ui/cs/advprog/eshop/repository/ProductRepository.java) sehingga bisa menambah implementasi baru (mis. DB/JPA) tanpa perlu mengubah controller/service.
- `IdGenerator` berbasis interface, sehingga strategi pembuatan id bisa ditambah (mis. incremental ID) tanpa mengubah logika create di repository.

### **LSP (Liskov Substitution Principle)**
Karena service bergantung pada interface [`id.ac.ui.cs.advprog.eshop.repository.ProductRepository`](src/main/java/id/ac/ui/cs/advprog/eshop/repository/ProductRepository.java),
maka implementasi lain yang memenuhi kontrak yang sama dapat menggantikan [`id.ac.ui.cs.advprog.eshop.repository.InMemoryProductRepository`](src/main/java/id/ac/ui/cs/advprog/eshop/repository/InMemoryProductRepository.java) tanpa merusak perilaku sistem (contoh: `findAll()`, `findProductById()`, `updateProduct()`, `deleteProduct()` tetap tersedia dan konsisten).

### **ISP (Interface Segregation Principle)**
Saat ini interface service masih digabung dalam [`id.ac.ui.cs.advprog.eshop.service.ProductService`](src/main/java/id/ac/ui/cs/advprog/eshop/service/ProductService.java).
Namun saya menghindari “client” (controller/test) bergantung ke detail repository secara langsung: controller tetap menggunakan service.
Catatan improvement jika ingin lebih ISP: memecah `ProductService` menjadi `ProductQueryService` dan `ProductCommandService` supaya client hanya tergantung method yang dibutuhkan sesuai namanya yaitu Query (akses database) dan Command (perintah)

### **DIP (Dependency Inversion Principle)**
High-level module (service/controller) bergantung pada abstraksi:
- Service bergantung pada interface repository: [`id.ac.ui.cs.advprog.eshop.service.ProductServiceImpl`](src/main/java/id/ac/ui/cs/advprog/eshop/service/ProductServiceImpl.java) -> [`id.ac.ui.cs.advprog.eshop.repository.ProductRepository`](src/main/java/id/ac/ui/cs/advprog/eshop/repository/ProductRepository.java)
- Controller bergantung pada interface service: [`id.ac.ui.cs.advprog.eshop.controller.ProductController`](src/main/java/id/ac/ui/cs/advprog/eshop/controller/ProductController.java) -> [`id.ac.ui.cs.advprog.eshop.service.ProductService`](src/main/java/id/ac/ui/cs/advprog/eshop/service/ProductService.java)


## 2) Keuntungan menerapkan SOLID pada project ini (dengan contoh)

1. **Lebih mudah di-test (unit test lebih stabil dan cepat)**
  Karena service tidak "terikat" ke detail penyimpanan, repository bisa di-*mock*. Contoh: test service menggunakan mock [`id.ac.ui.cs.advprog.eshop.repository.ProductRepository`](src/main/java/id/ac/ui/cs/advprog/eshop/repository/ProductRepository.java) di [`ProductServiceImplTest`](src/test/java/id/ac/ui/cs/advprog/eshop/ProductServiceImplTest.java).

2. **Perubahan tidak menimbulkan efek domino**
  Jika suatu saat ganti dari in-memory ke database, cukup buat implementasi baru untuk [`id.ac.ui.cs.advprog.eshop.repository.ProductRepository`](src/main/java/id/ac/ui/cs/advprog/eshop/repository/ProductRepository.java).
  Controller/service tidak harus ikut diubah, karena mereka bergantung ke interface.

3. **Lebih mudah menambah fitur (extend, bukan edit)**
  ContohL strategi id bisa diganti dengan menambahkan implementasi baru dari [`id.ac.ui.cs.advprog.eshop.util.IdGenerator`](src/main/java/id/ac/ui/cs/advprog/eshop/util/IdGenerator.java),
  tanpa mengutak-atik logika utama create di [`id.ac.ui.cs.advprog.eshop.repository.InMemoryProductRepository`](src/main/java/id/ac/ui/cs/advprog/eshop/repository/InMemoryProductRepository.java).



## 3) Kerugian jika tidak menerapkan SOLID (dengan contoh)

1. **Tight coupling -> susah diganti/di-maintain**
  Jika controller langsung akses struktur datar repository (misal list internal), perubahan kecil di repository bisa memaksa perubahan di banyak file.

2. **Sulit unit testing**
  Kalau service bergantung pada concrete class (bukan interface), akan sulit membuat mock/stub. Akibatnya test menjadi lebih lambat (cenderung jadi integration test) dan lebih rapuh.

3. **Perubahan kecil memicu banyak modifikasi (melanggar OCP)**
  Contoh: Apabila pembuatn id "hardcoded" UUID di banyak tempat, saat kebijakan id berubah, banyak file harus diubah dan risiko bug yang harus di solve jadi meningkat

4. **Interface terlalu besar, jadinya membebani client (risiko melanggar ISP)**
  Jika semua kebutuhan digabung dalam satu interface besar, client yang cuma butuh 'findAll()' tetap "dipaksa tahu" method lain (create/update/delete). Ini yang ngebuat desain makin sulit dirawat.

# Refleksi 5 (TDD & F.I.R.S.T.)

## 1) Refleksi terhadap workflow TDD

Menurut saya, workflow **Test-Driven Development (TDD)** yang saya jalankan pada tutorial ini **cukup berguna**, tetapi **belum sepenuhnya optimal**. TDD membantu saya membangun rasa aman saat melakukan perubahan kode, karena setelah menambah atau mengubah fitur, saya bisa segera melihat apakah perilaku lama masih berjalan dengan benar atau tidak. Hal ini terasa terutama pada pengujian di level **repository**, **service**, **controller**, dan **functional test**, karena setiap layer punya verifikasi yang berbeda.

Dari exercise ini, saya merasakan beberapa manfaat utama dari TDD:

- **Meningkatkan confidence saat refactor atau menambah fitur.**  
  Saat ada perubahan pada alur `create`, `update`, `delete`, dan `find`, keberadaan test membuat saya tidak terlalu takut merusak behavior yang sudah benar.
- **Membantu memecah masalah menjadi skenario kecil.**  
  Saya jadi lebih terbiasa memikirkan input, proses, dan output yang diharapkan sebelum menulis implementasi.
- **Membantu mendeteksi regression lebih cepat.**  
  Jika ada perubahan yang tidak sengaja merusak fungsi yang lama, test akan lebih cepat memberi sinyal dibanding hanya mengandalkan pengecekan manual.

Namun, setelah saya refleksikan, workflow saya **belum sepenuhnya “TDD murni”**. Dalam beberapa bagian, test terasa lebih seperti alat verifikasi setelah implementasi selesai, bukan benar-benar menjadi pemandu desain sejak awal. Contohnya, ada beberapa test yang lebih berorientasi pada **menutup branch coverage** atau memastikan implementasi tertentu terpanggil, bukan dimulai dari kebutuhan perilaku sistem yang paling penting. Jadi, walaupun saya sudah merasakan manfaat TDD, saya masih perlu meningkatkan konsistensi dalam menjalankan siklus **red -> green -> refactor**.

Hal yang perlu saya lakukan pada pengerjaan berikutnya adalah:

1. **Menulis failing test terlebih dahulu sebelum menulis implementasi.**  
   Saya ingin lebih disiplin memulai dari test yang merepresentasikan requirement, lalu baru menulis kode minimum agar test lolos.

2. **Fokus pada behavior, bukan detail implementasi.**  
   Test sebaiknya memverifikasi apa yang seharusnya dilakukan sistem, bukan terlalu bergantung pada cara internal implementasinya.

3. **Menambah test untuk edge case dan boundary case.**  
   Misalnya:
    - nama produk kosong,
    - kuantitas nol atau negatif,
    - product ID tidak ditemukan,
    - status order tidak valid,
    - update atau delete terhadap data yang tidak ada.

4. **Membedakan tujuan unit test dan functional test dengan lebih jelas.**  
   Unit test sebaiknya tetap menjadi alat utama untuk validasi logika bisnis secara cepat, sedangkan functional test cukup dipakai untuk alur penting end-to-end.

5. **Melakukan refactor pada test code juga.**  
   Jika nanti jumlah functional test bertambah, saya perlu mengurangi duplikasi setup dengan helper, base test class, atau pola seperti Page Object Model agar test tetap mudah dirawat.

Secara keseluruhan, saya menilai bahwa TDD pada tutorial ini **sudah cukup membantu**, terutama untuk membangun confidence dan menjaga kualitas perubahan kode. Namun, ke depannya saya ingin memakai TDD bukan hanya sebagai alat pengecekan, tetapi juga sebagai alat untuk **mengarahkan desain dan perilaku program sejak awal**.

---

## 2) Refleksi terhadap prinsip F.I.R.S.T.

Menurut saya, sebagian besar unit test yang saya buat **sudah cukup mengikuti prinsip F.I.R.S.T.**, tetapi masih ada beberapa bagian yang perlu diperbaiki.

### **F - Fast**
Sebagian besar **unit test** saya tergolong cepat, terutama test pada level model, repository, service, dan controller yang tidak membutuhkan proses berat. Test berbasis mock juga membantu eksekusi tetap ringan.

Namun, **functional test** berbasis Selenium tentu lebih lambat dibanding unit test biasa karena melibatkan browser, rendering halaman, dan mekanisme wait. Jadi, untuk aspek **Fast**, saya bisa mengatakan bahwa **unit test sudah cukup baik**, tetapi functional test memang secara alami lebih berat.

### **I - Independent**
Sebagian besar test saya sudah cukup independen karena memakai `@BeforeEach`, mock object, dan data uji yang disiapkan ulang untuk setiap test. Ini membantu agar satu test tidak bergantung pada hasil test lain.

Walaupun begitu, saya melihat masih ada ruang perbaikan, terutama pada **functional test** yang bergantung pada state aplikasi yang sedang berjalan. Memang saya sudah memakai data unik seperti `UUID`, tetapi pendekatan ini belum sebaik reset state secara eksplisit. Jadi, untuk prinsip **Independent**, saya menilai **cukup baik**, tetapi belum sempurna di semua layer.

### **R - Repeatable**
Sebagian besar test saya bisa dijalankan berulang kali dengan hasil yang konsisten, terutama test yang berbasis mock dan input deterministik.

Tantangan ada pada functional test karena ia lebih sensitif terhadap:
- environment browser,
- timing,
- redirect,
- dan kondisi runtime aplikasi.

Saya sudah menggunakan explicit wait agar test lebih stabil, tetapi tetap ada potensi flaky dibanding unit test biasa. Jadi, prinsip **Repeatable** sudah cukup terpenuhi pada unit test, tetapi masih perlu perhatian lebih pada functional test.

### **S - Self-Validating**
Menurut saya, test yang saya buat sudah cukup memenuhi prinsip ini karena hasil test ditentukan secara otomatis oleh assertion, bukan lewat pengecekan manual. Saya memakai `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotNull`, dan `verify()` untuk memastikan hasilnya jelas: test pass atau fail.

Namun, ke depannya saya ingin membuat assertion yang lebih kuat, bukan hanya memastikan method terpanggil, tetapi juga memastikan **perilaku bisnis** benar-benar sesuai requirement. Jadi, prinsip **Self-Validating** sudah berjalan dengan baik, tetapi masih bisa dibuat lebih tajam.

### **T - Timely**
Prinsip ini adalah bagian yang menurut saya masih paling perlu diperbaiki. Saya memang sudah membuat test untuk fitur-fitur yang dikerjakan, tetapi setelah refleksi, saya merasa belum selalu konsisten menulis test **sebelum** implementasi. Dalam beberapa kasus, test terasa ditambahkan setelah kode utama sudah terbentuk, atau setelah saya tahu branch mana yang ingin ditutup.

Artinya, dari sisi **Timely**, saya belum selalu konsisten menjalankan semangat TDD sepenuhnya. Ke depannya, saya harus lebih disiplin menulis test dari requirement terlebih dahulu, lalu baru menulis implementasi minimum yang diperlukan.

---

## Kesimpulan

Secara umum, saya menilai bahwa test yang saya buat **sudah cukup baik dan cukup membantu proses development**, terutama pada aspek validasi otomatis, keamanan saat refactor, dan pengurangan risiko regression. Untuk prinsip **F.I.R.S.T.**, test saya paling kuat pada aspek **Fast, Self-Validating,** dan sebagian **Independent**, tetapi masih perlu peningkatan pada aspek **Repeatable** untuk functional test, serta terutama pada aspek **Timely** agar lebih konsisten dengan praktik TDD yang sebenarnya.

Jika saya membuat test lagi di masa depan, mungkin bisa saya usahakan untuk:
- lebih disiplin menjalankan **red -> green -> refactor**,
- lebih fokus pada **behavior-driven test**,
- menambah **edge case** yang lebih realistis,
- dan menjaga test tetap **ringkas, stabil, independen, dan mudah dirawat**.