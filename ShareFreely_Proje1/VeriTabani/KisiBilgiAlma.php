<?php
$conn = mysqli_connect("localhost", "root", "", "sharefreely");
$kisiId=$_GET["kisiId"];
$sorgu=mysqli_query($conn, "select * from kisiler where id = '$kisiId'");

class Result{
    public $mail;
}
$result = new Result();
$data = mysqli_fetch_assoc($sorgu);
$result -> mail = $data["mail"];

echo (json_encode($result));

mysqli_close($conn);
?>
