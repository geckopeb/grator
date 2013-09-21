package controllers.operations

import java.io.File

object FileOperations{
  def createPath(path: String){
    val carpeta = new java.io.File(path)
    try{
      if(!carpeta.exists){
        carpeta.getParentFile().mkdirs();
      }
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
}