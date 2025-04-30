package org.example.hbank.api.utility

object QRCode {

    private const val FILE_QR_CODE_PATH = "./src/main/resources/static/images/"
    private const val FILE_FORMAT = "PNG"
    private const val QR_CODE_SIZE = 300

    /*
    @Throws(WriterException::class, IOException::class)
    fun generateQRCodeFile(content: String): ByteArray = try {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(
            content,
            BarcodeFormat.QR_CODE,
            QR_CODE_SIZE,
            QR_CODE_SIZE
        )
        val outputStream = ByteArrayOutputStream()
        val matrixToImageConfig = MatrixToImageConfig(
            MatrixToImageConfig.BLACK,
            MatrixToImageConfig.WHITE
        )
        MatrixToImageWriter.writeToStream(
            bitMatrix,
            FILE_FORMAT,
            outputStream,
            matrixToImageConfig
        )
        outputStream.toByteArray()
    } catch (exception: WriterException) {
        throw exception
    } catch (exception: IOException) {
        throw exception
    }

    @Throws(WriterException::class, IOException::class)
    fun generateQRCodeFile(content: String, filename: String) {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(
            content,
            BarcodeFormat.QR_CODE,
            QR_CODE_SIZE,
            QR_CODE_SIZE
        )
        val path: Path = FileSystems.getDefault().getPath("$FILE_QR_CODE_PATH$filename.png")
        MatrixToImageWriter.writeToPath(bitMatrix, FILE_FORMAT, path)
    }
    */

}
