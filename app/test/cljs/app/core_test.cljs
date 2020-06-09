(ns app.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [day8.re-frame.test :as rf-test]
            [re-frame.core :as rf]
            [app.db]
            [app.events :as events]
            [app.subs]))


(deftest app-test
  (rf-test/run-test-sync
    (rf/dispatch [::events/initialize-db])
    (let [active-filter (rf/subscribe [:filter-keyword])
          active-list (rf/subscribe [:active-list])
          visible-todos (rf/subscribe [:visible-todos])
          lists (rf/subscribe [:lists])]

      ;; Test initial state
      (is (= :all @active-filter))
      (is (empty? @active-list))
      (is (empty? @lists))
      (is (empty? @visible-todos))


      ;; Dispatch get lists and todos
      (rf/dispatch [:get-lists-success [{:id 1
                                         :title "Test List"}]])
      (rf/dispatch [:get-todos-success [{:id 1
                                         :title "Test Todo"
                                         :done false
                                         :list_id 1}
                                        {:id 2
                                         :title "Test Todo 2"
                                         :done true
                                         :list_id 1}
                                        {:id 3
                                         :title "Test Todo 3"
                                         :done false
                                         :list_id 1}]])
      ;; Test list count
      (is (= 1 (count @lists)))

      ;; Test add list
      (rf/dispatch [:add-list-success {:id 2
                                       :title "Test List 2"}])
      (is (= 2 (count @lists)))

      ;; Test set-active-list
      (rf/dispatch [:set-active-list-id 1])
      (is (= 1 (:id @active-list)))

      ;; Test todos count
      (is (= 3 (count @visible-todos)))

      ;; Test set-filter active
      (rf/dispatch [:set-filter :active])
      (is (= :active @active-filter))
      (is (= 2 (count @visible-todos)))

      ;; Test set-filter done
      (rf/dispatch [:set-filter :done])
      (is (= :done @active-filter))
      (is (= 1 (count @visible-todos)))

      ;; Test update-todo
      (rf/dispatch [:update-todo-success {:id 1
                                          :title "Test Todo 1"
                                          :list_id 1
                                          :done true}])
      (is (= 2 (count @visible-todos)))

      ;; Test add-todo
      (rf/dispatch [:add-todo-success {:id 4
                                       :title "Test Todo 4"
                                       :done true
                                       :list_id 1}])
      (is (= 3 (count @visible-todos)))

      ;; Test delete-todo
      (rf/dispatch [:delete-success :todos 4])
      (is (= 2 (count @visible-todos)))


      ;; Test delete-list
      (rf/dispatch [:delete-success :lists 1])
      (rf/dispatch [:delete-success :lists 2])
      (is (empty? @lists))
      )))
