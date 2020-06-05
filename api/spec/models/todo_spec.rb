require 'rails_helper'

RSpec.describe Todo, type: :model do
  it { should belong_to(:list) }
  it { should validate_presence_of(:title) }
end
