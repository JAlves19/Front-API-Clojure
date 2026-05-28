(ns calculadora-front.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json])
  (:gen-class))

(def api-url "http://localhost:3000")

(defn menu []
  (println "\nCalculadora de Calorias")
  (println "1. Cadastrar dados pessoais")
  (println "2. Consultar dados pessoais")
  (println "3. Registrar consumo de alimento")
  (println "4. Registrar atividade fisica")
  (println "5. Consultar extrato de transacoes")
  (println "6. Consultar saldo de calorias")
  (println "0. Sair")
  (print "\nEscolha uma opcao: ")
  (flush))

(defn ler-opcao
  ([]
   (read-line))
  ([mensagem]
   (print mensagem) (flush)
   (read-line)))

(defn- cadastrar-usuario []
  (let [altura (ler-opcao "Altura (cm): ")
        peso   (ler-opcao "Peso (kg): ")
        idade  (ler-opcao "Idade: ")
        sexo   (ler-opcao "Sexo (M/F): ")
        dados {:altura altura :peso peso :idade idade :sexo sexo}
        resposta (http/post (str api-url "/usuario")
                            {:content-type :json
                            :body (json/generate-str dados)})]
    (println "Cadastrando...")))


(defn- executar-opcao [opcao]
  (cond
    (= opcao "1") (cadastrar-usuario)
    (= opcao "2") (println "Consultar dados pessoais...")
    (= opcao "3") (println "Registrar alimento...")
    (= opcao "4") (println "Registrar atividade...")
    (= opcao "5") (println "Consultar extrato...")
    (= opcao "6") (println "Consultar saldo...")
    :else         (println "Opcao invalida! Tente novamente.")))

(defn- executar-menu []
  (menu)
  (let [opcao (ler-opcao)]
    (when (not= opcao "0")
      (executar-opcao opcao)
      (recur))))


(defn -main [& args]
  (executar-menu))