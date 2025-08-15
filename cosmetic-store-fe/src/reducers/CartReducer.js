export default function cartReducer(state, action) {
    switch (action.type) {
        case "set": 
            return action.payload;
        case "increment": 
            return state + 1;
        case "decrement": 
            return Math.max(0, state - 1);
        default:
            return state;
    }
}
