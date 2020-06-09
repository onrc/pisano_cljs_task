require 'rails_helper'

RSpec.describe "/todos", type: :request do
  let!(:list) { create(:list) }
  let!(:todos) { create_list(:todo, 10, list_id: list.id ) }

  let(:list_id) { list.id }
  let(:todo_id) { todos.first.id }

  describe "GET /index" do
    before { get list_todos_url(list_id) }

    it "returns a successful response" do
      expect(response).to be_successful
    end

    it "returns all todo items" do
      expect(json.size).to eq(10)
    end
  end

  describe "GET /show" do
    before { get list_todo_url(list_id, todo_id) }

    context "with valid todo_id" do
      it "returns a successful response" do
        expect(response).to be_successful
        expect(json['id']).to eq(todo_id)
        expect(json['list_id']).to eq(list_id)
      end
    end

    context "with invalid todo_id" do
      let(:todo_id) { todos.last.id + 1 }

      it "returns an unsuccessful response" do
        expect(response).to have_http_status(:not_found)
      end
    end
  end

  describe "POST /create" do
    let(:title) { "Manuscript loathsome cyclopean eldritch." }
    let(:params) { { todo: { title: title, done: false } } }

    context "with valid parameters" do
      before { post list_todos_url(list_id), params: params }

      it "creates a todo" do
        expect(json["title"]).to eq(title)
        expect(json["list_id"]).to eq(list_id)
      end

      it "returns http status code 201" do
        expect(response).to have_http_status(:created)
      end
    end

    context "with invalid parameters" do
      let(:params) { { todo: { title: "" } } }
      before { post list_todos_url(list_id), params: params }

      it "returns http status code 422" do
        expect(response).to have_http_status(:unprocessable_entity)
      end
    end
  end

  describe "PUT /update" do
    let(:title) { "Foetid gibbous madness daemoniac." }
    let(:params) { { todo: { done: true, title: title } } }

    context "with valid parameters" do
      before { put list_todo_url(list_id, todo_id), params: params }

      it "returns http status code 200" do
        expect(response).to have_http_status(:ok)
      end

      it "returns updated status" do
        expect(json["done"]).to eq(true)
        expect(json["title"]).to eq(title)
      end
    end

    context "with invalid parameters" do
      let(:params) { { todo: { title: "" } } }
      before { put list_todo_url(list_id, todo_id), params: params }

      it "returns http status code 422" do
        expect(response).to have_http_status(:unprocessable_entity)
      end
    end
  end

  describe "DELETE /destroy" do
    before { delete list_todo_url(list_id, todo_id) }

    it "returns valid http response" do
      expect(response).to have_http_status(:no_content)
    end
  end
end
