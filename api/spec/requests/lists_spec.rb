require 'rails_helper'

RSpec.describe "/lists", type: :request do
  let!(:lists) { create_list(:list, 5) }
  let(:list_id) { lists.first.id }

  describe "GET /index" do
    before { get lists_url }

    it "returns a successful response" do
      expect(response).to be_successful
    end

    it "returns all list items" do
      expect(json.size).to eq(5)
    end
  end

  describe "GET /show" do
    before { get list_url(list_id) }

    context "with valid list_id" do
      it "returns a successful response" do
        expect(response).to be_successful
        expect(json).not_to be_empty
        expect(json['id']).to eq(list_id)
      end
    end

    context "with invalid list_id" do
      let(:list_id) { lists.last.id + 1 }

      it "returns an unsuccessful response" do
        expect(response).to have_http_status(:not_found)
      end

      it "returns an error message" do
        expect(json).to match({"error" => "Couldn't find List with 'id'=#{list_id}"})
      end
    end
  end

  describe "POST /create" do
    let(:params) { { list: { title: 'Arkham' } } }

    context "with valid parameters" do
      before { post lists_url, params: params  }

      it "creates a list" do
        expect(json['title']).to eq('Arkham')
      end

      it "returns http status code 201" do
        expect(response).to have_http_status(:created)
      end
    end

    context "with invalid parameters" do
      let(:params) { { list: { title: "" } } }
      before { post lists_url, params: params }

      it "returns http status code 422" do
        expect(response).to have_http_status(:unprocessable_entity)
      end
    end
  end

  describe "PUT /update" do
    let(:params) { { list: { title: "Kingsport" } } }

    context "with valid parameters" do
      before { put list_url(list_id), params: params }

      it "returns http status code 200" do
        expect(response).to have_http_status(:ok)
      end

      it "returns updated title" do
        expect(json["title"]).to eq("Kingsport")
      end
    end

    context "with invalid parameters" do
      let(:params) { { list: { title: "" } } }
      before { put list_url(list_id), params: params }

      it "returns http status code 422" do
        expect(response).to have_http_status(:unprocessable_entity)
      end
    end
  end

  describe "DELETE /destroy" do
    before { delete list_url(list_id) }

    it "returns valid http response" do
      expect(response).to have_http_status(:no_content)
    end
  end
end
