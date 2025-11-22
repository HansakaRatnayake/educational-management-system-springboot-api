package com.lezord.system_api.util;

import com.lezord.system_api.exception.BadRequestException;
import com.lezord.system_api.exception.InternalServerException;
import org.springframework.stereotype.Component;

import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;

@Component
public class FileDataHandler {


    public String extractActualFileName(InputStreamReader data) {
        StringBuilder result = new StringBuilder();
        try (BufferedReader bufReader = new BufferedReader(data)) {
            String line;
            while ((line = bufReader.readLine()) != null) result.append(line);
            return result.toString().trim();

        } catch (IOException exception) {
            throw new InternalServerException(String.format("Error while reading file name : %s", exception.getMessage()));
        }

    }


    public String stringToBinary(String input) {
        if (input == null || input.isEmpty()) throw new BadRequestException( "Input string is empty");

        StringBuilder binaryStringBuilder = new StringBuilder();
        for (char c : input.toCharArray()) {
            String binary = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'); // Ensure 8-bit format
            binaryStringBuilder.append(binary).append(" ");
        }
        return binaryStringBuilder.toString().trim();
    }


    public byte[] blobToByteArray(Blob blob){
        if (blob == null) throw new BadRequestException( "blob is empty");

        try (InputStream inputStream = blob.getBinaryStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[(int) blob.length()];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }catch (IOException | SQLException exception) {
            throw new InternalServerException(String.format("Error while converting blob to byte array : %s", exception.getMessage()));
        }
    }


    public Blob byteArrayToBlob(byte[] byteArray) throws SQLException {
        if (byteArray == null || byteArray.length == 0) throw new BadRequestException( "byteArray is empty");
        return new SerialBlob(byteArray);
    }


    public String blobToString(Blob blob) {
        if (blob == null) throw new BadRequestException( "blob is empty");

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(blob.getBinaryStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (SQLException | IOException exception) {
            throw new InternalServerException(String.format("Error while converting blob to string : %s", exception.getMessage()));
        }
        return stringBuilder.toString().trim();
    }


    public String byteArrayToString(byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) throw new BadRequestException( "blob is empty");
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    public byte[] stringToByteArray(String input) {
        if (input == null || input.isEmpty()) {
            throw new BadRequestException("Input string is empty");
        }

        return input.getBytes(StandardCharsets.UTF_8);
    }
}
