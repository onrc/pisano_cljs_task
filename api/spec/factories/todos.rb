FactoryBot.define do
  factory :todo do
    title { Faker::Books::Lovecraft.sentence }
    done { false }
    list_id { nil }
  end
end
