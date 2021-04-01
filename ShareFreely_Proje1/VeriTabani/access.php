<?php


class access
{
    //baglantı için gerekli degiskenlerimiz

    private $host = null;
    private $user = null;
    private $pass = null;
    private $name = null;
    private $conn = null;   //database ismi
//yapıcı metot
    function __construct($dbhost, $dbuser, $dbpass, $dbname)
    {
        $this->host = $dbhost;//atama işlemi javadaki . yerine -> kullanılıyor
        $this->user = $dbuser;
        $this->pass = $dbpass;
        $this->name = $dbname;
    }

    function connect()//veritabanı baglantısı fonksiyonu
    {
        $this->conn = new mysqli($this->host, $this->user, $this->pass, $this->name);//veri tabanına baglan
        $this->conn->set_charset("utf8");

        if (!$this->conn)//hata varrsa
            //echo "Bağlantı başarısız";
            return false;

        return true;
    }


    function disconnect()//veritabanı baglantısını kapat
    {

        if ($this->conn != null)
            $this->conn->close();
    }


    // Kullanıcı kaydı
    public function registerUser($kullaniciAdi, $guvenli_sifre, $salt, $mail, $adSoyad)
    {
        // sql sorgusu
        $sql = "INSERT INTO kisiler SET kullaniciAdi=?, sifre=?, salt=?, mail=?, adSoyad=?";

        // sorgu sonuçları için hazırlık
        $statement = $this->conn->prepare($sql);

        // hata varsa
        if (!$statement) {
            throw new Exception($statement->error);
        }

        // sql komutuna yerleştirilecek olan değerlerin bağlanması
        $statement->bind_param("sssss", $kullaniciAdi, $guvenli_sifre, $salt, $mail, $adSoyad);

        // sorguyu çalıştır
        $returnValue = $statement->execute();

        return $returnValue;

    }

    // Kullanıcı bilgilerini öğren
    public function selectUser($kullaniciAdi)
    {
        $returArray = array();

        // sql sorgusu
        $sql = "SELECT * FROM kisiler WHERE kullaniciAdi='" . $kullaniciAdi . "'";

        // sql sorgu sonucunu $result değişkeninde tutuyoruz
        $result = $this->conn->query($sql);

        // en az bir sonuç dönerse
        if ($result != null && (mysqli_num_rows($result) >= 1)) {

            // ilişkisel dizi olarak $row değişkeninde sakla
            // MYSQLI_ASSOC => Dizinin indis değerleri ise veritabanı tablomuzdaki sütun isimleridir.
            $row = $result->fetch_array(MYSQLI_ASSOC);

            if (!empty($row)) {
                $returArray = $row;
            }

        }

        return $returArray;
    }

    public function saveToken($tablo, $token, $id)
    {
        // sql sorgusu
        $sql = "INSERT INTO $tablo SET id=?, token=?";

        // sorgu sonuçları için hazırlık
        $statement = $this->conn->prepare($sql);

        // hata varsa
        if (!$statement) {
            throw new Exception($statement->error);
        }

        // sql komutuna yerleştirilecek olan değerlerin bağlanması
        $statement->bind_param("is", $id, $token);

        // sorguyu çalıştır
        $returnValue = $statement->execute();

        return $returnValue;
    }

    // token ile kullanıcı id sini alma işlemi
    function getUserId($tablo,$token) {

        $returnArray = array();

        // sql sorgusu
        $sql = "SELECT id FROM $tablo WHERE token = '".$token."'";

        // sorgu sonuçları için hazırlık
        $result = $this->conn->query($sql);

        // sonuç boş değilse
        if ($result != null && (mysqli_num_rows($result) >= 1)) {

            // sonuç içeriği assoc dizisine dönüştürülür ve $row değişkenine kaydedilir
            $row = $result->fetch_array(MYSQLI_ASSOC);

            if (!empty($row)) {
                $returnArray = $row;
            }
        }

        return $returnArray;

    }


//emailonay  durumunu değiştir
    function emailOnayDurumunuDegistir($id, $durum)
    {

        $sql = "UPDATE kisiler SET emailOnay=? WHERE id=?";
        $statement = $this->conn->prepare($sql);

        if (!$statement) {
            throw new Exception($statement->error);
        }

        $statement->bind_param("ii", $durum, $id);

        $returnValue = $statement->execute();

        return $returnValue;

    }


    // token silinecek
    function deleteToken($tablo, $token)
    {

        $sql = "DELETE FROM $tablo WHERE token=?";
        $statement = $this->conn->prepare($sql);

        if (!$statement) {
            throw new Exception($statement->error);
        }
        $statement->bind_param("s", $token);

        $returnValue = $statement->execute();

        return $returnValue;

    }


    // profil resminin yolunun veritabanına kaydedilmesi
    function updateProfilFotoPath($path, $id)

    {

        // sql komutu
        $sql = "UPDATE kisiler SET avatar=?  WHERE id=?";

        // sorgu sonuçları için hazırlık
        $statement = $this->conn->prepare($sql);

        // hata olursa
        if (!$statement) {
            throw new Exception($statement->error);
        }

        // sql deyimine parametreleri bağla
        $statement->bind_param("si", $path, $id);

        // sonucu diziye atama işlemi
        $returnValue = $statement->execute();

        return $returnValue;

    }


    // Id ye göre kullanıcı bilgisini alma
    public function selectUserIdyeGore($id)
    {

        $returnArray = array();

        // sql komutu
        $sql = "SELECT * FROM kisiler WHERE id='" . $id . "'";

        //  aldığımız sonucu $result değerine atama işlemi
        $result = $this->conn->query($sql);

        // gerekli kontroller
        if ($result != null && (mysqli_num_rows($result) >= 1)) {

            // aldığımız sonuçları ilişkisel dizi olarak $row değişkenine atama işlemi
            $row = $result->fetch_array(MYSQLI_ASSOC);

            //kontrol iyidir
            if (!empty($row)) {
                $returnArray = $row;
            }

        }

        return $returnArray;

    }


    // Insert tweet sadece text
    public function tweet_kaydet_text($id, $uuid, $text)
    {

        // sql sorgusu
        $sql = "INSERT INTO gonderiler SET id=?, uuid=?, text=?";

        // hazırlık
        $statement = $this->conn->prepare($sql);

        // hata olursa
        if (!$statement) {
            throw new Exception($statement->error);
        }

        // parametreler giriliyor
        $statement->bind_param("iss", $id, $uuid, $text);

        // sorguyu çalıştırma ve sonucu $returnValue ye aktarma
        $returnValue = $statement->execute();

        return $returnValue;

    }


    // Insert tweet sadece resim
    public function tweet_kaydet_resim($id, $uuid, $path)
    {

        // sql sorgusu
        $sql = "INSERT INTO gonderiler SET id=?, uuid=?, path=?";

        // hazırlık
        $statement = $this->conn->prepare($sql);

        // hata olursa
        if (!$statement) {
            throw new Exception($statement->error);
        }

        // parametreler giriliyor
        $statement->bind_param("iss", $id, $uuid, $path);

        // sorguyu çalıştırma ve sonucu $returnValue ye aktarma
        $returnValue = $statement->execute();

        return $returnValue;

    }

    // Insert tweet text ve resim
    public function tweet_kaydet_text_resim($id, $uuid, $text, $path)
    {

        // sql sorgusu
        $sql = "INSERT INTO gonderiler SET id=?, uuid=?, text=?, path=?";

        // hazırlık
        $statement = $this->conn->prepare($sql);

        // hata olursa
        if (!$statement) {
            throw new Exception($statement->error);
        }

        // parametreler giriliyor
        $statement->bind_param("isss", $id, $uuid, $text, $path);

        // sorguyu çalıştırma ve sonucu $returnValue ye aktarma
        $returnValue = $statement->execute();

        return $returnValue;

    }

    public function getKisiTweetleri($id)
    {

        $returnArray = array();

        //sql sorgusu
        $sql = "SELECT gonderiler.date , 
      gonderiler.id , 
      gonderiler.path , 
      gonderiler.text , 
      gonderiler.uuid , 
      kisiler.adSoyad , 
      kisiler.avatar,  
      kisiler.id, 
      kisiler.kullaniciAdi, 
      kisiler.mail 
      FROM sharefreely.gonderiler JOIN sharefreely.kisiler ON 
     gonderiler.id=$id AND kisiler.id=$id ORDER BY gonderiler.date DESC LIMIT 50";

        // sorguyu hazırlama
        $statement = $this->conn->prepare($sql);

        // hata kontrolü
        if (!$statement) {
            throw new Exception($statement->error);
        }

        // sql sorgusunu çalıştır
        $statement->execute();

        // sonuç $result değişkenine aktarılıyor
        $result = $statement->get_result();

        // yeni satır bulunduğu sürece returnArray e ekler
        while ($row = $result->fetch_assoc()) {
            $returnArray[] = $row;
        }


        return $returnArray;
    }


    public function deleteTweet($uuid)
    {

        // sql sorgusu
        $sql = "DELETE FROM gonderiler WHERE uuid = ?";


        $statement = $this->conn->prepare($sql);

        // hata varsa
        if (!$statement) {
            throw new Exception($statement->error);
        }

        // parametreleri ver ve çalıştır
        $statement->bind_param("s", $uuid);
        $statement->execute();

        // silinmş satır sayısını alıyoruz
        $returnValue = $statement->affected_rows;

        return $returnValue;

    }


//arama işlemi ile kullanıcı bigisini verecek
    public function selectUsers($word, $id)
    {

        //veritabanından döndürülen tüm bilgileri depolamak için dizi değişkeni tanımlıyoruz
        $returnArray = array();

        // kelime girilmemiş ise çalıştırılacak sql sorgusu
        $sql = "SELECT id, kullaniciAdi, mail, adSoyad, avatar FROM kisiler WHERE NOT id = $id";

        // kelime girildiyse sql deyimini daha geniş arama için değiştiriyoruz
        if (!empty($word)) {
            $sql .= " AND ( kullaniciAdi LIKE ? OR adSoyad LIKE ? )";
        }

        // hazırlık
        $statement = $this->conn->prepare($sql);

        // hata oluşursa
        if (!$statement) {
            throw new Exception($statement->error);
        }

        // kelime girildiyse parametre ekliyoruz
        if (!empty($word)) {
            $word = '%' . $word . '%';
            $statement->bind_param("ss", $word, $word);
        }

        // sorguyu çalıştır
        $statement->execute();

        // döndürülen sonuçları $result değişkenine ata
        $result = $statement->get_result();

        // $result'u assoc dizisine dönüştürdüğümüzde her döngüde $row'a atanır
        while ($row = $result->fetch_assoc()) {

            // yeni satır bulunduğu sürece returnArray e ekler
            $returnArray[] = $row;
        }

        // geribildirim sonucu
        return $returnArray;

    }


// Maile  göre kullanıcı bilgisini alma
    public function selectUserMaileGore($mail)
    {

        $returnArray = array();

        // sql komutu
        $sql = "SELECT * FROM kisiler WHERE mail='" . $mail . "'";

        //  aldığımız sonucu $result değerine atama işlemi
        $result = $this->conn->query($sql);

        // gerekli kontroller
        if ($result != null && (mysqli_num_rows($result) >= 1)) {

            // aldığımız sonuçları ilişkisel dizi olarak $row değişkenine atama işlemi
            $row = $result->fetch_array(MYSQLI_ASSOC);

            //kontrol iyidir
            if (!empty($row)) {
                $returnArray = $row;
            }

        }

        return $returnArray;

    }


    //şifre güncelleniyor
    public function sifreGuncelle($id, $guvenli_sifre, $salt)
    {


        $sql = "UPDATE kisiler SET sifre=?, salt=? WHERE id=?";
        $statement = $this->conn->prepare($sql);

        if (!$statement) {
            throw new Exception($statement->error);
        }

        $statement->bind_param("ssi", $guvenli_sifre, $salt, $id);

        $returnValue = $statement->execute();

        return $returnValue;


    }

}
?>