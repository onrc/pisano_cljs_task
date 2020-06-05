require 'rails_helper'

RSpec.describe List, type: :model do
  pending "add some examples to (or delete) #{__FILE__}"
  it { should have_many(:todos).dependent(:destroy) }
  it { should validate_presence_of(:title) }
end
