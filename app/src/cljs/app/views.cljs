(ns app.views
  (:require
    [reagent.core :as r]
    [re-frame.core :refer [dispatch subscribe]]
    [app.subs :as subs]
    [clojure.string :as str]))


(defn input-form
  [{:keys [title placeholder action on-save on-stop]}]
  (let [initial (-> title str)
        value (r/atom title)
        stop #(do
                (reset! value "")
                (if on-stop (on-stop)))]
    (fn []
      [:form.input-form {:on-submit #(do
                                       (.preventDefault %)
                                       (on-save @value)
                                       (stop))}
       [:button.btn.submit {:type "submit"
                            :disabled (or
                                        (empty? (-> @value str str/trim))
                                        (= initial @value))}
        (if (= action :edit) [:i.fas.fa-check] [:i.fas.fa-plus])]
       [:input.todo-input {:type "text"
                           :placeholder placeholder
                           :value @value
                           :auto-focus true
                           :on-change #(reset! value (-> % .-target .-value))
                           :on-key-down #(case (.-which %)
                                           27 (stop)
                                           nil)}]])))


(defn list-item
  [{:keys [id title]}]
  (let [active-list (subscribe [:active-list])]
    [:li {:class (when (= id (:id @active-list)) "active")}
     [:span.title {:on-click #(dispatch [:get-todos id])}
      [:i.list-icon.fas.fa-bars] title]
     [:button.delete {:type "button"
                      :on-click #(dispatch [:delete-list id])} [:i.fas.fa-times-circle]]]))


(defn todo-item
  []
  (let [editing (r/atom false)]
    (fn [{:keys [id done title]}]
      (if @editing
        [input-form {:title title
                     :action :edit
                     :on-save #(dispatch [:update-todo id {:title %}])
                     :on-stop #(swap! editing not)}]
        [:div.todo-item
         [:input.toggle {:type "checkbox"
                         :checked done
                         :on-change #(dispatch [:update-todo id {:done (-> % .-target .-checked)}])}]
         [:label title]
         [:button.btn.edit {:on-click #(swap! editing not)} [:i.far.fa-edit]]
         [:button.btn.delete {:on-click #(dispatch [:delete-todo id])}
          [:i.far.fa-times-circle]]]))))


(defn filter-button
  [filter-keyword title]
  (let [active-filter (subscribe [:filter-keyword])]
    [:button {:on-click #(dispatch [:set-filter filter-keyword])
              :class (when (= filter-keyword @active-filter) "active")} title]))


(defn left-pane
  []
  (let [lists @(subscribe [:lists])]
    [:div.left-pane
     [:ul.lists (for [item lists]
                  ^{:key (:id item)} [list-item item])]
     [input-form {:on-save #(dispatch [:add-list {:title %}])
                  :placeholder "Add new list"}]]))


(defn right-pane
  [active-list]
  (let [todos (subscribe [:visible-todos])]
    [:div.right-pane
     [:h2.list-title (:title active-list) " " [:small (-> (:created_at active-list)
                                                          (js/Date.)
                                                          (.toDateString))]]
     [:div.todo-wrapper
      [input-form {:key (:id active-list)
                   :on-save #(dispatch [:add-todo {:title %
                                                   :done false}])
                   :placeholder "Add new todo"}]
      [:div.list
       (for [todo @todos]
         ^{:key (:id todo)} [todo-item todo])]
      [:div.filters
       [filter-button :all "All"]
       [filter-button :active "Active"]
       [filter-button :done "Done"]]]]))


(defn main-panel
  []
  (let [active-list (subscribe [:active-list])]
    [:div.panel
     [left-pane]
     (when-not (nil? @active-list)
       [right-pane @active-list])]))
