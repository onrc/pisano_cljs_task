(ns app.events
  (:require
   [re-frame.core :as re-frame]
   [app.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [ajax.core :as ajax]
   [clojure.string :as str]
   ))

;; helpers

(defn index-by-id [coll]
    (into {} (map (juxt :id identity) coll)))

;; events

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))


(re-frame/reg-event-fx
  :get-lists
  (fn
    [{db :db} _]
    {:http-xhrio {:method          :get
                  :uri             "http://localhost:3000/lists"
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:get-lists-success]
                  :on-error        [:handle-error]}
     }))


(re-frame/reg-event-db
  :get-lists-success
  (fn
    [db [_ response]]
    (assoc db :lists (index-by-id response))
    ))

(re-frame/reg-event-fx
  :add-list
  (fn
    [{db :db} [_ params]]
    {:http-xhrio {:method          :post
                  :uri             "http://localhost:3000/lists"
                  :params          params
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:add-list-success]
                  :on-error        [:handle-error]}
     }))

(re-frame/reg-event-db
  :add-list-success
  (fn
    [db [_ response]]
    (assoc-in db [:lists (:id response)] response)
    ))

(re-frame/reg-event-fx
  :delete-list
  (fn
    [{db :db} [_ id]]
    {:http-xhrio {:method          :delete
                  :uri             (str "http://localhost:3000/lists/" id)
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:delete-success :lists id]
                  :on-error        [:handle-error]}
     }))

(re-frame/reg-event-db
  :delete-success
  (fn
    [db [_ coll id response]]
    (update-in db [coll] dissoc id)
    ))


(re-frame/reg-event-db
  :set-active-list-id
  (fn
    [db [_ id]]
    (assoc db :active-list-id id)))


(re-frame/reg-event-fx
  :get-todos
  (fn
    [cofx [_ id]]
    {:http-xhrio {:method          :get
                  :uri             (str "http://localhost:3000/lists/" id "/todos")
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:get-todos-success]
                  :on-error        [:handle-error]}
     :dispatch [:set-active-list-id id]}))


(re-frame/reg-event-db
  :get-todos-success
  (fn
    [db [_ response]]
    (assoc db :todos (index-by-id response))
    ))


(re-frame/reg-event-fx
  :add-todo
  (fn
    [{db :db} [_ params]]
    {:http-xhrio {:method          :post
                  :uri             (str "http://localhost:3000/lists/" (:active-list-id db) "/todos")
                  :params          params
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:add-todo-success]
                  :on-error        [:handle-error]}
     }))

(re-frame/reg-event-db
  :add-todo-success
  (fn
    [db [_ response]]
    (assoc-in db [:todos (:id response)] response)
    ))


(re-frame/reg-event-fx
  :delete-todo
  (fn
    [{db :db} [_ id]]
    {:http-xhrio {:method          :delete
                  :uri             (str "http://localhost:3000/lists/" (:active-list-id db) "/todos/" id)
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:delete-success :todos id]
                  :on-error        [:handle-error]}
     }))

(re-frame/reg-event-fx
  :update-todo
  (fn
    [{db :db} [_ id params]]
    {:http-xhrio {:method          :put
                  :uri             (str "http://localhost:3000/lists/" (:active-list-id db) "/todos/" id)
                  :params params
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:update-todo-success]
                  :on-error        [:handle-error]}
     }))


(re-frame/reg-event-db
  :update-todo-success
  (fn
    [db [_ response]]
    (assoc-in db [:todos (:id response)] response)
    ))

(re-frame/reg-event-db
  :set-filter
  (fn
    [db [_ filter-keyword]]
    (assoc db :filter filter-keyword)
    ))

(re-frame/reg-event-db
  :handle-error
  (fn [db [_ error]]
    (print error)))
