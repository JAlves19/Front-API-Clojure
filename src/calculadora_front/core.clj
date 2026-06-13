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
  (let [altura   (ler-opcao "Altura (cm): ")
        peso     (ler-opcao "Peso (kg): ")
        idade    (ler-opcao "Idade: ")
        sexo     (ler-opcao "Sexo (M/F): ")
        dados    {:altura altura :peso peso :idade idade :sexo sexo}
        resposta (http/post (str api-url "/usuario")
                            {:content-type :json
                             :body         (json/generate-string dados)})]
    (println "Usuario cadastrado com sucesso!")))

(defn- consultar-usuario []
  (let [resposta (http/get (str api-url "/usuario"))
        dados    (json/parse-string (:body resposta) true)]
    (println "\n=== Dados Pessoais ===")
    (println (str "Altura: " (:altura dados) " cm"))
    (println (str "Peso:   " (:peso dados) " kg"))
    (println (str "Idade:  " (:idade dados) " anos"))
    (println (str "Sexo:   " (:sexo dados)))))

(defn- registrar-alimento []
  (let [alimento    (ler-opcao "Nome do alimento: ")
        quantidade  (ler-opcao "Quantidade (g): ")
        data        (ler-opcao "Data (YYYY-MM-DD): ")
        dados       {:alimento alimento :quantidade quantidade :data data}
        resposta    (http/post (str api-url "/alimento")
                               {:content-type :json
                                :body         (json/generate-string dados)})]
    (println "Alimento registrado com sucesso!")))

(defn- registrar-atividade []
  (let [atividade  (ler-opcao "Nome da atividade: ")
        duracao    (ler-opcao "Duracao (minutos): ")
        data       (ler-opcao "Data (YYYY-MM-DD): ")
        dados      {:atividade atividade :duracao duracao :data data}
        resposta   (http/post (str api-url "/exercicio")
                              {:content-type :json
                               :body         (json/generate-string dados)})]
    (println "Atividade registrada com sucesso!")))

(defn- consultar-extrato []
  (let [data-inicio (ler-opcao "Data inicio (YYYY-MM-DD): ")
        data-fim    (ler-opcao "Data fim (YYYY-MM-DD): ")
        resposta    (http/get (str api-url "/extrato")
                              {:query-params {:data-inicio data-inicio
                                              :data-fim    data-fim}})
        transacoes  (json/parse-string (:body resposta) true)]
    (println "\n=== Extrato ===")
    (dorun (map (fn [t]
              (println (str (:data t) " | "
                            (if (= (:tipo t) "ganho") "GANHO" "PERDA")
                            " | "
                            (if (= (:tipo t) "ganho")
                              (str (:descricao t) " (" (:quantidade t) "g)")
                              (:descricao t))
                            " | " (:calorias t) " kcal")))
            transacoes))))

(defn- consultar-saldo []
  (let [data-inicio (ler-opcao "Data inicio (YYYY-MM-DD): ")
        data-fim    (ler-opcao "Data fim (YYYY-MM-DD): ")
        resposta    (http/get (str api-url "/saldo")
                              {:query-params {:data-inicio data-inicio
                                              :data-fim    data-fim}})
        dados       (json/parse-string (:body resposta) true)]
    (println "\n=== Saldo de Calorias ===")
    (println (str "Saldo: " (:saldo dados) " kcal"))))

(defn- executar-opcao [opcao]
  (cond
    (= opcao "1") (cadastrar-usuario)
    (= opcao "2") (consultar-usuario)
    (= opcao "3") (registrar-alimento)
    (= opcao "4") (registrar-atividade)
    (= opcao "5") (consultar-extrato)
    (= opcao "6") (consultar-saldo)
    :else         (println "Opcao invalida! Tente novamente.")))

(defn- executar-menu []
  (menu)
  (let [opcao (ler-opcao)]
    (when (not= opcao "0")
      (executar-opcao opcao)
      (recur))))


(defn -main [& args]
  (executar-menu))