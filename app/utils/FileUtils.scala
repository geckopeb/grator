package utils

import java.io.{File,FileInputStream,FileOutputStream}
import org.apache.commons.io.{FileUtils => FU}

object FileUtils{
  def createPath(path: String){
    val carpeta = new java.io.File(path)
    try{
      if(!carpeta.exists){
        carpeta.getParentFile().mkdirs();
      }
    } catch {
      case e: Exception => println("The following exception was raised: " + e);  
    }
  }

  def writeToFile(path: String, file: String) {
    this.createPath(path)
    val archivo = new java.io.PrintWriter(new File(path))
    try {
      archivo.write(file)
    } finally {
      archivo.close()
    }
  }

  def copy(source: String, destination: String): Unit = {
    val src = new File(source)
    val dest = new File(destination)

    FU.copyDirectory(src, dest)
  }
}