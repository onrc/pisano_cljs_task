FactoryBot.define do
  factory :list do
    title { Faker::Books::Lovecraft.location }
  end
end
