(ns sanatoriocolegiales.conversion
  (:require [clojure.java.io :as io]
            [pdfboxing.text :as text]
            [clojure.string :as s]
            [babashka.fs :as fs]
            [clojure.stacktrace :as st]))
(import java.io.IOException)

(defn- pdf-a-txt
  "Recibe el nombre/ruta del archivo pdf como string y devuelve un archivo txt"
  [archivo destino]
  (try
    #_(println "Argumentos obtenidos en función pdf->txt. Archivo: " archivo " Destino: " destino)
    (let [txt (text/extract archivo)
          outputname (as-> archivo arch
                       (if (fs/windows?) (s/split arch #"\\") (s/split arch #"/"))
                       (s/split (last arch) #"\.")
                       (first arch)
                       (str destino arch ".txt"))]
      #_(println "Nombre de output: "outputname)
      (with-open [w (io/writer outputname)]
        (.write w txt))
      (println "Archivo creado "outputname))
    (catch IOException e (prn (.getMessage e)))))

(defn- to-file 
  [dir]
  (try
    (let [proc (->> (fs/list-dir dir "*.pdf")
                    (map fs/file)
                    (map #(.getAbsolutePath %)))]
      (println "Archivos a procesar  => " proc)
      proc)
    (catch IOException e (st/root-cause e))))

(defn- obtener-archivos
  [dir]
  (try 
    (if (fs/directory? dir)
      (to-file dir)
      (println "El input <origen> debe ser un directorio")) 
    (catch IOException e (st/root-cause e))))
  
(defn convertir
  "Recibe un directorio de origen y convierte todos los pdf en archivos txt guardándolos en el directorio de destino"
  [{:keys [_arguments]}]
  (let [[origen destino] _arguments]
    #_(println "Argumentos recibidos por API expuesta. Origen: " origen " Destino: " destino)
    (try
      (doseq [arch (obtener-archivos origen)]
        (pdf-a-txt arch destino))
      (catch IOException e (prn "Surgió una excepción: " (.getMessage e))))))

(comment
  (def arch "c:/Users/jrivero/Documentos para convertir a txt/Prueba1.pdf")
  (def arch2 "c:\\Users\\jrivero\\personal_factura.pdf")
  (as-> arch2 arch
    (if (fs/windows?) (s/split arch #"\\") (s/split arch #"/"))
    (s/split (last arch) #"\.")
    (first arch)
    (str "c:/Users/jrivero/" arch ".txt"))
  (obtener-archivos "lachingado/")
  (obtener-archivos "c:/Users/jrivero/Documentos para convertir a txt/")
  (obtener-archivos "c:/Users/jrivero/")
  (pdf->txt "c:/Users/jrivero/Documentos para convertir a txt/Prueba1.pdf" "c:/Users/jrivero/")
  (pdf->txt "c:\\Users\\jrivero\\P Graham On Lisp.pdf" "c:/Users/jrivero/")

  (convertir {:_arguments ["c:/Users/jrivero/Documentos para convertir a txt/" "c:/Users/jrivero/Documentos para convertir a txt/"]})
  ;; Debo usar una función que tolere efectos secundarios, por eso el fallo misterioso con estas dos funciones
  (let [origen "c:/Users/jrivero/Documentos para convertir a txt/"
        destino origen]
    #_(print "Con threading...")
    #_(time (->> (obtener-archivos origen)
                 (map #(pdf-a-txt % destino))))
    (print "Con transductor...")
    (time (eduction (map #(pdf-a-txt % destino)) (obtener-archivos origen))))
  )
