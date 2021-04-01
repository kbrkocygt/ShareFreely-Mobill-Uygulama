
<?php

$returnArray=array();

if (empty($_REQUEST["id"]) || empty($_REQUEST["uuid"]) || empty($_REQUEST["istek_turu"])) {
    $returnArray["status"] = "203";
    $returnArray["mesaj"] = "Gerekli bilgi veya bilgiler eksik.";
    echo json_encode($returnArray);
    return;
}

$id = htmlentities($_REQUEST["id"]);
$uuid= htmlentities($_REQUEST["uuid"]);
$istek_turu= htmlentities($_REQUEST["istek_turu"]);



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


$path = "http://192.168.1.42/ShareFreely/gonderiler/" . $id . "/gonderi-" . $uuid . ".jpg"; // ...ShareFreely/gonderiler/12/gonderi-9029fb70-c584-439f-8617-45e17109ddf5.jpg

switch ($istek_turu) {
    case '1':
        //sadece resim
        $returnArray=resimYukle();

        if ($returnArray["status"]=="200") {
            $access->tweet_kaydet_resim($id, $uuid,$path);
        }


        break;

    case '2':
        //sadece text
        $text= htmlentities($_REQUEST["text"]);
        $access->tweet_kaydet_text($id, $uuid, $text);
        $returnArray["status"] = "200";
        $returnArray["mesaj"] = "Tweet başarılı bir şekilde kaydedildi.";
        break;

    case '3':
        //hem text hem resim
        $text= htmlentities($_REQUEST["text"]);
        $returnArray=resimYukle();


        if ($returnArray["status"]=="200") {
            $access->tweet_kaydet_text_resim($id, $uuid, $text, $path);
        }


        break;
}


function resimYukle()
{
    $resim= htmlentities($_REQUEST["resim"]);
    $id = htmlentities($_REQUEST["id"]);
    $uuid= htmlentities($_REQUEST["uuid"]);
    $returnArray=array();

    // Kullanınıcı için id adında bir klasör yolu oluşturma işlemi
    $folder = "C:/xampp/htdocs/ShareFreely/gonderiler/" . $id;


    // böyle bir dosya yoksa, oluşturur
    if (!file_exists($folder)) {
        mkdir($folder, 0777,true);
    }


    $resim_yukleme_yolu="gonderiler/" . $id. "/gonderi-" . $uuid . ".jpg";

    //profil resmini yükleme işlemi
    $result=file_put_contents($resim_yukleme_yolu,base64_decode($resim));

    if ($result) {
        $returnArray["status"] = "200";
        $returnArray["mesaj"] = "Tweet resmi başarılı bir şekilde yüklendi.";

    }else{
        $returnArray["status"] = "300";
        $returnArray["mesaj"] = "Tweet resmi yüklenirken hata oluştu.";

    }

    return $returnArray;

}





$access->disconnect();

echo json_encode($returnArray);




?>



