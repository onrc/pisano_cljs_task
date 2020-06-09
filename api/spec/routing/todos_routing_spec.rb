require "rails_helper"

RSpec.describe TodosController, type: :routing do
  describe "routing" do
    it "routes to #index" do
      expect(get: "/lists/1/todos").to route_to("todos#index", list_id: "1")
    end

    it "routes to #show" do
      expect(get: "/lists/1/todos/1").to route_to("todos#show", list_id: "1", id: "1")
    end


    it "routes to #create" do
      expect(post: "/lists/1/todos").to route_to("todos#create", list_id: "1")
    end

    it "routes to #update via PUT" do
      expect(put: "/lists/1/todos/1").to route_to("todos#update", list_id: "1", id: "1")
    end

    it "routes to #update via PATCH" do
      expect(patch: "/lists/1/todos/1").to route_to("todos#update", list_id: "1", id: "1")
    end

    it "routes to #destroy" do
      expect(delete: "/lists/1/todos/1").to route_to("todos#destroy", list_id: "1", id: "1")
    end
  end
end
