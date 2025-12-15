import { useState } from "react";

export default function BookingHistoryTabs({ onTypeChange, isLoading }) {
    const [selected, setSelected] = useState("ship");

    const handleClick = (type) => {
        setSelected(type);
        onTypeChange(type);
    };

    return (
        <div className="flex space-x-2">
            <button
                className={`px-6 py-2 rounded-lg transition-all duration-200 ${
                    selected === "ship"
                        ? "bg-pink-500 text-white font-bold shadow-md"
                        : "bg-white border border-pink-200 text-pink-500 hover:bg-pink-50"
                }`}
                onClick={() => handleClick("ship")}
            >
                Du thuyền
            </button>
            <button
                className={`px-6 py-2 rounded-lg transition-all duration-200 ${
                    selected === "hotel"
                        ? "bg-pink-500 text-white font-bold shadow-md"
                        : "bg-white border border-pink-200 text-pink-500 hover:bg-pink-50"
                }`}
                onClick={() => handleClick("hotel")}
            >
                Khách sạn
            </button>
        </div>
    );
}
