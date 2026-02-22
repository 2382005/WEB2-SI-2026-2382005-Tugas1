Latihan 1

Eksperimen 1:

Buka /test/view → Apa yang muncul? Ini dari @Controller 
Buka /test/text → Apa yang muncul? Ini dari @Controller + @ResponseBody → text langsung
Apa perbedaannya? Kalau yang /test/view hanya dari @Controller sedangkan yang /test/text ditambah dari @ResponseBody
Kesimpulan: 
@Controller tanpa @ResponseBody → return nama template (nama template/data langsung). Dengan @ResponseBody → return data langsung (nama template/data langsung).

Eksperimen 2:
Apakah berhasil? [Ya/Tidak] Tidak
HTTP Status code: 500
Error message: Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.
Sun Feb 22 16:22:19 WIB 2026
There was an unexpected error (type=Internal Server Error, status=500).

Kesimpulan: Jika Controller return nama view yang tidak ada, Spring akan mengembalikan error “This application has no explicit mapping for /error” karena karena halaman yang harus ditampilkan tidak ada.

Eksperimen 3:

URL	Hasil di Halaman
/greet	Selamat Pagi, Mahasiswa!

/greet?name=Budi	Selamat Pagi, Budi!

/greet?name=Budi&waktu=Siang	Selamat Siang, Budi!

/greet/Ani	Halo, Ani!

/greet/Ani/detail	Halo, Ani!

/greet/Ani/detail?lang=EN	Hello, Ani!


Pertanyaan:
•	URL mana yang pakai @RequestParam? /greet?name=Budi, /greet?name=Budi&waktu=Siang
•	URL mana yang pakai @PathVariable? /greet/Ani, /greet/Ani/detail, /greet/Ani/detail
•	URL mana yang pakai keduanya? /greet

Eksperimen 4:
ProductService:
public List<String> getAllCategories() {
    return products.stream()
            .map(Product::getCategory)
            .distinct()
            .toList();
}
public long countByCategory(String category) {
    return products.stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .count();
}

ProductController:
@GetMapping("/categories")
public String categories(Model model) {

    List<String> categories = productService.getAllCategories();

    model.addAttribute("categories", categories);
    model.addAttribute("title", "Daftar Kategori");

    return "product/categories";
}

categories.html:
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Categories</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<div class="container mt-5">
    <h2 th:text="${title}"></h2>

    <ul class="list-group mt-3">

        <li class="list-group-item d-flex justify-content-between align-items-center"
            th:each="cat : ${categories}">

            <!-- Link ke /products/category/{nama} -->
            <a th:href="@{/products/category/{c}(c=${cat})}"
               th:text="${cat}">
            </a>

            <!-- Badge jumlah produk -->
            <span class="badge bg-primary rounded-pill"
                  th:text="${@productService.countByCategory(cat)}">
            </span>

        </li>

    </ul>

</div>

</body>
</html>

Pertanyaan Refleksi
1.	Apa perbedaan antara @Controller dan @RestController? Dalam kasus apa kamu pakai masing-masing? 
Jawaban: @Controller digunakan untuk menampilkan halaman HTML, sedangkan @RestController digunakan untuk mengembalikan data JSON untuk API.

2.	Perhatikan bahwa template product/list.html dipakai oleh 3 endpoint berbeda (list all, filter by category, search). Apa keuntungannya membuat template yang reusable seperti ini?
Jawaban: Template reusable mengurangi duplikasi kode, memudahkan perawatan, dan membuat tampilan tetap konsisten.
3.	Kenapa Controller inject ProductService (bukan langsung akses data di ArrayList)? Apa yang terjadi kalau Controller langsung manage data?
Jawaban: Controller menginject ProductService agar tugas terpisah dengan jelas dan kode lebih rapi serta mudah dikembangkan.
4.	Apa perbedaan model.addAttribute("products", products) dengan return products langsung seperti di @RestController?
Jawaban: model.addAttribute() mengirim data ke view (HTML), sedangkan return langsung pada @RestController mengirim data sebagai JSON.
5.	Jika kamu buka http://localhost:8080/products/abc (ID bukan angka), apa yang terjadi? Kenapa?
Jawaban: Jika membuka /products/abc akan terjadi error 400 karena "abc" tidak bisa dikonversi menjadi tipe Long.
6.	Apa keuntungan pakai @RequestMapping("/products") di level class dibanding menulis full path di setiap @GetMapping?
Jawaban: @RequestMapping("/products") di level class membuat kode lebih ringkas dan menghindari penulisan path yang berulang.
7.	Dalam lab ini, kata "Model" muncul dalam beberapa konteks berbeda. Sebutkan minimal 2 arti yang berbeda dan jelaskan perbedaannya. 
Jawaban: Kata “Model” bisa berarti wadah pengirim data ke view, layer data dalam folder model/, atau class representasi objek seperti Product.


Latihan 2

Eksperimen 1:
Apakah error? [Ya/Tidak] Ya
Error message: Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.
Sun Feb 22 18:05:09 WIB 2026
There was an unexpected error (type=Internal Server Error, status=500).

Eksperimen 2:
1.	Jalankan dan buka halaman. Apakah CSS masih ter-load?
CSS masih bekerja?         [Ya/Tidak] Ya
2.	Sekarang coba path yang salah:
<link rel="stylesheet" th:href="@{/css/tidak-ada.css}">
Apakah halaman error?      [Ya/Tidak] Tidak
Apakah CSS diterapkan?     [Ya/Tidak] Ya

Kesimpulan: 
th:href="@{}" lebih baik karena dapat menghasilkan URL yang dinamis dan menyesuaikan dengan context path secara otomatis . Jika file CSS tidak ada, untuk projek ini style masih ada dan tidak hilang.



Eksperimen 3:
ProductService:
public long getTotalProducts() {
    return products.size();
}

public Map<String, Long> getProductCountByCategory() {
    return products.stream()
            .collect(Collectors.groupingBy(
                    Product::getCategory,
                    Collectors.counting()
            ));
}

public Optional<Product> getMostExpensiveProduct() {
    return products.stream()
            .max(Comparator.comparing(Product::getPrice));
}

public Optional<Product> getCheapestProduct() {
    return products.stream()
            .min(Comparator.comparing(Product::getPrice));
}

public double getAveragePrice() {
    return products.stream()
            .mapToDouble(Product::getPrice)
            .average()
            .orElse(0);
}

public long countLowStockProducts() {
    return products.stream()
            .filter(p -> p.getStock() < 20)
            .count();
}

StatisticsController:
@Controller
public class StatisticsController {

    private final ProductService productService;

    public StatisticsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {

        model.addAttribute("totalProducts", productService.getTotalProducts());
        model.addAttribute("categoryStats", productService.getProductCountByCategory());
        model.addAttribute("mostExpensive", productService.getMostExpensiveProduct().orElse(null));
        model.addAttribute("cheapest", productService.getCheapestProduct().orElse(null));
        model.addAttribute("averagePrice", productService.getAveragePrice());
        model.addAttribute("lowStockCount", productService.countLowStockProducts());

        return "statistics";
    }
}


statistics.html:
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Statistik</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="d-flex flex-column min-vh-100">

<div th:replace="~{fragments/layout :: navbar}"></div>

<div class="container mt-5 flex-fill">

    <h2>Statistik Produk</h2>

    <p>Total Produk: <strong th:text="${totalProducts}"></strong></p>

    <h4>Total Produk per Kategori:</h4>
    <ul>
        <li th:each="entry : ${categoryStats}">
            <span th:text="${entry.key}"></span> :
            <span th:text="${entry.value}"></span>
        </li>
    </ul>

    <p>
        Produk Termahal:
        <span th:text="${mostExpensive.name}"></span> -
        Rp <span th:text="${mostExpensive.price}"></span>
    </p>

    <p>
        Produk Termurah:
        <span th:text="${cheapest.name}"></span> -
        Rp <span th:text="${cheapest.price}"></span>
    </p>

    <p>Rata-rata Harga: Rp <span th:text="${averagePrice}"></span></p>

    <p>Jumlah Produk Stok < 20: <strong th:text="${lowStockCount}"></strong></p>

</div>

<div th:replace="~{fragments/layout :: footer}"></div>

</body>
</html>

Pertanyaan Refleksi
1.	Apa keuntungan menggunakan Thymeleaf Fragment untuk navbar dan footer?
Thymeleaf Fragment membuat navbar dan footer bisa dipakai ulang di banyak halaman sehingga tidak perlu menulis kode yang sama berulang-ulang dan lebih mudah dirawat.
2.	Apa bedanya file di static/ dan templates/? Kenapa CSS ada di static/ bukan templates/?
Folder static/ berisi file statis seperti CSS dan JS yang langsung diakses browser, sedangkan templates/ berisi file HTML yang diproses oleh Thymeleaf, sehingga CSS diletakkan di static/ karena tidak perlu diproses template engine.
3.	Apa yang dimaksud dengan th:replace dan bagaimana bedanya dengan th:insert? 
o	Hint: coba ganti th:replace jadi th:insert dan inspect element di browser
th:replace mengganti seluruh tag dengan fragment, sedangkan th:insert hanya memasukkan isi fragment ke dalam tag yang ada.


4.	Kenapa kita pakai @{} untuk URL di Thymeleaf, bukan langsung tulis path?
Kita memakai @{} karena Thymeleaf akan menyesuaikan URL dengan context path aplikasi secara otomatis sehingga lebih aman dan fleksibel.
5.	Perhatikan bahwa ProductController inject ProductService melalui Constructor Injection (konsep dari Week 3). Apa jadinya kalau Controller tidak pakai DI dan langsung new ProductService() di dalam Controller?
Jika Controller langsung new ProductService(), maka kode menjadi tidak fleksibel, sulit dites, dan tidak mengikuti prinsip Dependency Injection yang membuat aplikasi lebih rapi dan scalable.

