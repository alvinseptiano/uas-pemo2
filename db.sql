create database pemo2;
use pemo2;
create table UAS (
	kode_produk int auto_increment,
	nama_produk varchar(100),
	jumlah_produk int,
	harga_produk int,
 	primary key(kode_produk)
);