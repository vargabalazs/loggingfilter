/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.loggingfilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;

/**
 *
 * @author Franck
 */
public class LogResponseStream extends ServletOutputStream {

  private ServletOutputStream output = null;
  private ByteArrayOutputStream baos = null;
  private boolean closed = false;

  public LogResponseStream(ServletOutputStream output) {
    this.output = output;
    this.baos = new ByteArrayOutputStream();
  }

  @Override
  public void close() throws IOException {
    if (this.closed) {
      throw new IOException("This output stream has already been closed");
    }

    byte[] arrayOfByte = this.baos.toByteArray();

    System.out.print("content : '" + this.baos + "'");

    this.output.write(arrayOfByte);
    this.output.flush();
    this.output.close();
    this.closed = true;
  }

  @Override
  public void flush()
          throws IOException {
    if (this.closed) {
      throw new IOException("Cannot flush a closed output stream");
    }
    this.baos.flush();
  }

  @Override
  public void write(int abyte ) throws IOException {
    if (this.closed) {
      throw new IOException("Cannot write to a closed output stream");
    }
    this.baos.write((byte) abyte);
  }

  @Override
  public void write(byte[] paramArrayOfByte)
          throws IOException {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  @Override
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
          throws IOException {
    System.out.println("writing...");
    if (this.closed) {
      throw new IOException("Cannot write to a closed output stream");
    }
    this.baos.write(paramArrayOfByte, paramInt1, paramInt2);
  }

}
