package dispatch

const (
	Status_OK          = 200
	Status_BAD_REQUEST = 400
	Status_UNKNOWN     = 500
	Status_TIMEOUT     = 510
)

type ActionNode struct {
	OrderId         string `json:"orderId"`
	ActionType      int    `json:"actionType"`
	ActionTimestamp int64  `json:"ActionTimestamp"`
}

type Location struct {
	Latitude  float64 `json:"latitude"`
	Longitude float64 `json:"longitude"`
}

type Courier struct {
	AreaId   string    `json:"areaId"`
	Id       string    `json:"id"`
	Loc      *Location `json:"loc"`
	Speed    float64   `json:speed`
	MaxLoads int       `json:"maxLoads"`
}

type CourierPlan struct {
	CourierId  string       `json:"courierId"`
	PlanRoutes []ActionNode `json:planRoutes`
}

type Order struct {
	AreaId                             string    `json:"areaId"`
	Id                                 string    `json:"id"`
	SrcLoc                             *Location `json:"srcLoc"`
	DstLoc                             *Location `json:"dstLoc"`
	Status                             int       `json:"status"`
	CreateTimestamp                    int64     `json:"createTimestamp"`
	PromiseDeliverTime                 int64     `json:"promiseDeliverTime"`
	EstimatedPrepareCompletedTimestamp int64     `json:"estimatedPrepareCompletedTimestamp"`
}

type DispatchRequest struct {
	RequestTimestamp int64     `json:"requestTimestamp"`
	AreaId           string    `json:"areaId"`
	IsFirstRound     bool      `json:"isFirstRound"`
	IsLastRound      bool      `json:"isLastRound"`
	Couriers         []Courier `json:"couriers"`
	Orders           []Order   `json:"orders"`
}

type DispatchSolution struct {
	CourierPlans []CourierPlan `json:"courierPlans"`
}

type Response struct {
	Code    int         `json:"code"`
	Result  interface{} `json:"result"`
	Message string      `json:"message"`
}

func NewResponse(v interface{}) *Response {
	return &Response{
		Code:    Status_OK,
		Result:  v,
		Message: "",
	}
}

func NewErrorResponse(code int, message string) *Response {
	return &Response{
		Code:    code,
		Message: message,
	}
}
