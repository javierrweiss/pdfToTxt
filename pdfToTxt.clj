(ns sanatoriocolegiales.pdfToTxt
  (:require [cli-matic.core :refer [run-cmd]]
            [sanatoriocolegiales.conversion :refer [convertir]])
  (:gen-class))

(def CONFIGURATION
  {:app {:command "pdf-a-txt"
         :description "Lee archivos en pdf del directorio de origen y los convierte en archivos txt en el directorio de destino"
         :version "0.0.1"} 
   :commands [{:command "exec" :short "e"
               :examples ["java -jar sanatoriocolegiales.pdfToTxt-0.1.0-SNAPSHOT.jar e '/user/Docs/' '/home/fulano/txts/'"]
               :runs convertir}]})

(defn -main
  "Lee los archivos pdf de la carpeta de origen y los guarda en la carpeta de destino en formato txt"
  [& args]
  #_(println "Argumentos recibidos por linea de comandos " args)
  (run-cmd args CONFIGURATION))

