
<?php

// bilgilerin boş olup olmadığının kontrolü
if (!empty($_POST["sifre_1"]) && !empty($_POST["sifre_2"]) && !empty($_POST["token"])) {

    $sifre_1 = htmlentities($_POST["sifre_1"]);
    $sifre_2 = htmlentities($_POST["sifre_2"]);
    $token = htmlentities($_POST["token"]);


    // şifreler eşit ise
    if ($sifre_1 == $sifre_2) {


        //veritabanı bağlantısı
        require("../access.php");
        $access = new access("localhost", "root", "", "sharefreely");
        $baglanti=$access->connect();

        if (!$baglanti)
        {
            $returnArray["status"] = "404";
            $returnArray["mesaj"] = "Bağlantı başarısız.";
            echo json_encode($returnArray);
            return;
        }


        // token aracılığıyla kullanıcı id sini alma işlemi
        $user = $access->getUserID("sifresifirlamatokens", $token);


        // veritabanında güncelleme işlemi
        if (!empty($user)) {

            // güvenli şifre oluşturuluyor
            $salt = openssl_random_pseudo_bytes(20);
            $guvenli_sifre = sha1($sifre_1 . $salt);

            // şifre güncelleniyor
            $result = $access->sifreGuncelle($user["id"], $guvenli_sifre, $salt);

            if ($result) {

                // token siliniyor
                $access->deleteToken("sifresifirlamatokens", $token);
                $mesaj = "Yeni şifre başarıyla oluşturuldu";

                header("Location:sifreSifirlandi.php?mesaj=" . $mesaj);

            } else {
                echo 'Kullanıcı bulunamadı';
            }


        }

    } else {
        $mesaj = "Şifreler uyuşmuyor";
    }

}



?>



<html>
<head>
    <!--Başlık-->
    <title>Yeni şifre oluştur</title>
    <!--Türkçe karakterlerin görülebilmesi için-->
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <!--CSS Sitilleri-->
    <style>

        .sifre_alani
        {
            margin: 10px;
        }

        .button
        {
            margin: 10px;
        }

    </style>

</head>


<body>
<h1>Yeni şifre oluştur</h1>

<?php
if (!empty($mesaj)) {
    echo "</br>" . $mesaj. "</br>";
}
?>

<!--Form-->
<form method="POST" action="<?php $_SERVER['PHP_SELF'];?>">
    <div><input type="password" name="sifre_1" placeholder="Yeni şifreniz" class="sifre_alani"/></div>
    <div><input type="password" name="sifre_2" placeholder="Şifre tekrar" class="sifre_alani"/></div>
    <div><input type="submit" value="Kaydet" class="button"/></div>
    <input type="hidden" value="<?php echo $_GET['token'];?>" name="token">
</form>

</body>

</html>