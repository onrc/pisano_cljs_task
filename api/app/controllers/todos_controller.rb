class TodosController < ApplicationController
  before_action :set_list
  before_action :set_todo, only: [:show, :update, :destroy]

  # GET /todos
  def index
    render json: @list.todos
  end

  # GET /todos/1
  def show
    render json: @todo
  end

  # POST /todos
  def create
    @todo = @list.todos.build(todo_params)

    if @todo.save
      render json: @todo, status: :created
    else
      render json: @todo.errors, status: :unprocessable_entity
    end
  end

  # PATCH/PUT /todos/1
  def update
    if @todo.update(todo_params)
      render json: @todo
    else
      render json: @todo.errors, status: :unprocessable_entity
    end
  end

  # DELETE /todos/1
  def destroy
    @todo.destroy
  end

  private
    def set_list
      @list = List.find(params[:list_id])
    end

    # Use callbacks to share common setup or constraints between actions.
    def set_todo
      @todo = @list.todos.find_by!(id: params[:id]) if @list
    end

    # Only allow a trusted parameter "white list" through.
    def todo_params
      params.require(:todo).permit(:title, :done)
    end
end
