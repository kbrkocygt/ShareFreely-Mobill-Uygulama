<?php
$returnArray=array();

if (empty($_REQUEST["id"]) || empty($_REQUEST["profil"])) {
    $returnArray["mesaj"] = "Gerekli bilgiler eksik";
    $returnArray["status"] = "203";
    echo json_encode($returnArray);
    return;
}

$profil = htmlentities($_REQUEST["profil"]);
$id = htmlentities($_REQUEST["id"]);




// Kullanınıcı için id adında bir klasör yolu oluşturma işlemi
$folder = "C:/xampp/htdocs/ShareFreely/kisiler/" . $id;


// böyle bir dosya yoksa, oluşturur
if (!file_exists($folder)) {
    mkdir($folder, 0777,true);
}





//veritabanı bağlantısı
require ("access.php");
$access = new access("localhost", "root", "", "sharefreely");
$baglanti=$access->connect();

if (!$baglanti)
{
    $returnArray["status"] = "404";
    $returnArray["mesaj"] = "Bağlantı başarısız.";
    echo json_encode($returnArray);
    return;
}


// Yüklenen dosyanın yolu veritabanına kaydediliyor
$path = "http://192.168.1.42/ShareFreely/kisiler/" . $id . "/profil.jpg";
$resim_yukleme_yolu="kisiler/" . $id. "/profil.jpg";

//profil resminin yolunun veritabanına kaydedilmesi
$access->updateProfilFotoPath($path, $id);


//profil resmini yükleme işlemi
$result=file_put_contents($resim_yukleme_yolu,base64_decode($profil));

if ($result) {
    $returnArray["status"] = "200";
    $returnArray["mesaj"] = "Profil resmi başarılı bir şekilde güncellendi.";
}else{
    $returnArray["status"] = "300";
    $returnArray["mesaj"] = "Profil resmi yüklenirken hata oluştu.";
}


// Güncelledikten sonra yeni kullanıcı bilgileri
$user = $access->selectUserIdyeGore($id);

$returnArray["id"] = $user["id"];
$returnArray["kullaniciAdi"] = $user["kullaniciAdi"];
$returnArray["adSoyad"] = $user["adSoyad"];
$returnArray["mail"] = $user["mail"];
$returnArray["avatar"] = $user["avatar"];



// bağlantıyı kapat
$access->disconnect();


// Uygulama kullanıcısına geri bildirim dizisi gönder
echo json_encode($returnArray);
?>
