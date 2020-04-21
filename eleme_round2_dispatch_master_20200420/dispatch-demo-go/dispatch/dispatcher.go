package dispatch

type Dispatcher interface {
	Ping() string
	Dispatch(request *DispatchRequest) ([]DispatchSolution, error)
}

type GreedyDispatcher struct {
	routes []DispatchSolution
}

func NewGreedyDispatcher() *GreedyDispatcher {
	return &GreedyDispatcher{}
}

func (*GreedyDispatcher) Ping() string {
	return "PONG"
}

func (*GreedyDispatcher) Dispatch(request *DispatchRequest) ([]DispatchSolution, error) {
	return nil, nil
}
