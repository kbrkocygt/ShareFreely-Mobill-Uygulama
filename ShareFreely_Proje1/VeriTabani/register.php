<?php

$returnArray=array();
//gelen verilerin dolu mu bos mu kontrolu
if (empty($_REQUEST["kullaniciAdi"]) || empty($_REQUEST["sifre"]) || empty($_REQUEST["mail"]) || empty($_REQUEST["adSoyad"])) {
    $returnArray["status"] = "203";//bossa mesaj ver
    $returnArray["mesaj"] = "Gerekli bilgi veya bilgiler eksik.";
    echo json_encode($returnArray);
    return;
}


$kullaniciAdi = htmlentities($_REQUEST["kullaniciAdi"]);//verileri aktarıyoruz
$sifre = htmlentities($_REQUEST["sifre"]);
$mail = htmlentities($_REQUEST["mail"]);
$adSoyad = htmlentities($_REQUEST["adSoyad"]);

$salt = openssl_random_pseudo_bytes(20);//20 karakterlik rastgele sifre olusturuyor
$guvenli_sifre = sha1($sifre . $salt);//şifreleme yöntemi

//veritabanı bağlantısı
require ("access.php");//içe aktardık import gibi
$access = new access("localhost", "root", "", "sharefreely");
$baglanti=$access->connect(); //
//baglantı kontrolu
if (!$baglanti)
{
    $returnArray["status"] = "404";
    $returnArray["mesaj"] = "Bağlantı başarısız.";
    echo json_encode($returnArray);
    return;
}

// veritabanına kullanıcı kayıt ediliyor
$result = $access->registerUser($kullaniciAdi, $guvenli_sifre, $salt, $mail, $adSoyad);


if ($result){
//başarılı mı

    // kulanıcı adına göre sorgula ve kişi bilgilerini getir
    $user = $access->selectUser($kullaniciAdi);//kullaniciadina göre kontrol saglıyo

    // kullanıcı bilgilerini dizide depolayıp daha sonra json olarak döndüreceğiz.
    $returnArray["status"] = "200";
    $returnArray["mesaj"] = "Kullanıcı kaydı başarılı bir şekilde yapıldı.";
    $returnArray["id"] = $user["id"];
    $returnArray["kullaniciAdi"] = $user["kullaniciAdi"];
    $returnArray["mail"] = $user["mail"];
    $returnArray["adSoyad"] = $user["adSoyad"];
   // $returnArray["avatar"] = $user["avatar"];

    require ("email.php");
    $email=new email();
    $token=$email->tokenOlustur(20);
    $access->saveToken("emailtokens",$token,$user["id"]);

    //mail gönderilecek
    $detay=array();
    $detay["konu"]="Share Freely için mail doğrulama işlemi.";
    $detay["kime"]=$user["mail"];
    $detay["fromName"]="Share Freely";
    $detay["fromEmail"]="kcygt.kbra@gmail.com";


    $sablon=$email->emailOnaySablonu();
    $sablon=str_replace("{token}",$token,$sablon);
    $detay["body"]=$sablon;

    $result=$email->sendEmail($detay);
    $returnArray["mailgondermedurumu"] = $result;


}else {

    $returnArray["status"] = "400";
    $returnArray["mesaj"] = "Verilen bilgilerle kayıt yapılamadı. Kullanıcı adı veya mail adresi daha önce kullanılmış.";

}

// bağlantıyı kapatıyoruz
$access->disconnect();


// json olarak veriyi döndürüyoruz
echo json_encode($returnArray);





?>