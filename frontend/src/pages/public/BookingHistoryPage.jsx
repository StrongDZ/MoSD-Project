import { useState } from "react";
import BookingHistoryTabs from "../../components/public/BookingHistoryTab";
import BookingList from "../../components/public/BookingList";
import BookingDetailModal from "../../components/public/BookingDetailModal";

export default function BookingHistoryPage() {
    const [selectedType, setSelectedType] = useState("ship");

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="container mx-auto px-4 py-6">
                <div className="flex gap-6">
                    {/* Sidebar */}
                    <div className="w-48 flex-shrink-0">
                        <div className="bg-white rounded-xl shadow-sm p-4 sticky top-6">
                            <div className="flex flex-col space-y-2">
                                <button
                                    className={`px-4 py-3 rounded-lg transition-all duration-200 text-left ${
                                        selectedType === "ship"
                                            ? "bg-pink-400 text-white font-bold shadow-md"
                                            : "bg-white border border-pink-200 text-pink-500 hover:bg-pink-50"
                                    }`}
                                    onClick={() => setSelectedType("ship")}
                                >
                                    Du thuy?n
                                </button>
                                <button
                                    className={`px-4 py-3 rounded-lg transition-all duration-200 text-left ${
                                        selectedType === "hotel"
                                            ? "bg-pink-400 text-white font-bold shadow-md"
                                            : "bg-white border border-pink-200 text-pink-500 hover:bg-pink-50"
                                    }`}
                                    onClick={() => setSelectedType("hotel")}
                                >
                                    Khách s?n
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Main Content */}
                    <div className="flex-1">
                        <div className="bg-white rounded-xl shadow-sm p-6">
                            <p className="text-gray-500 text-center py-8">Select a category to view booking history</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
