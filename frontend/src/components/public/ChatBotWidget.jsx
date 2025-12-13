import React, { useState, useRef, useEffect } from "react";
import { FiMessageSquare, FiSend, FiX } from "react-icons/fi";
import MarkdownIt from "markdown-it";
import { useAuth } from "../../contexts/AuthProvider";
import config from "../../config";
import { axiosRequest } from "../../utils/axiosUtils";

const ChatBotWidget = ({ isOpen, setIsOpen }) => {
    const [messages, setMessages] = useState([{ id: 1, text: "Hey there 👋\nHow can I help you today?", sender: "bot" }]);
    const [inputValue, setInputValue] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const messagesEndRef = useRef(null);
    const { token } = useAuth();

    const md = new MarkdownIt({
        html: true,
        linkify: true,
        breaks: true,
        typographer: true,
    });

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const handleSendMessage = async (e) => {
        e.preventDefault();
        const trimmedInput = inputValue.trim();
        if (!trimmedInput) return;

        const newMessage = { id: messages.length + 1, text: trimmedInput, sender: "user" };
        setMessages((prev) => [...prev, newMessage]);
        setInputValue("");
        setIsLoading(true);

        try {
            const response = await axiosRequest({
                url: `${config.api.url}/api/chatbot`,
                method: "POST",
                data: { message: trimmedInput },
                headers: {
                    "Content-Type": "application/json",
                },
                token: token,
            });

            console.log("🚨 Raw response from server:", response);

            if (!response.responseCode === 200) {
                throw new Error(`Server error ${response.responseCode}: ${response.message}`);
            }

            let data;
            try {
                data = JSON.parse(response.data.data);
            } catch (err) {
                console.error("❌ JSON parse error:", err);
                throw new Error("Invalid JSON from server");
            }

            const botMessage = data.data;
            const botResponse = {
                id: messages.length + 2,
                text: botMessage || "Sorry, I couldn't process that request.",
                sender: "bot",
            };
            setMessages((prev) => [...prev, botResponse]);
        } catch (error) {
            console.error("Error sending message:", error);
            setMessages((prev) => [
                ...prev,
                {
                    id: messages.length + 2,
                    text: "❌ Error: Unable to get response from server.",
                    sender: "bot",
                },
            ]);
        } finally {
            setIsLoading(false);
        }
    };

    const toggleChat = () => setIsOpen(!isOpen);

    if (!isOpen) {
        return (
            <button
                onClick={toggleChat}
                className="fixed bottom-5 right-5 bg-gradient-to-r from-pink-500 to-pink-600 text-white p-4 rounded-full shadow-lg hover:scale-110 transition-transform duration-200 z-[9999]"
                aria-label="Open Chat"
            >
                <FiMessageSquare size={24} />
            </button>
        );
    }

    return (
        <>
            {/* Chat Widget */}
            <div className="fixed bottom-5 right-5 w-80 md:w-96 h-[500px] flex flex-col bg-white rounded-lg shadow-2xl z-[9999] border border-gray-200">
                {/* Header */}
                <div className="flex items-center justify-between p-3 bg-gradient-to-r from-pink-300 to-pink-400 text-white rounded-t-lg">
                    <div className="flex items-center space-x-2">
                        <FiMessageSquare size={20} />
                        <span className="font-semibold">Chatbot</span>
                    </div>
                    <button onClick={toggleChat} aria-label="Close Chat">
                        <FiX size={20} className="hover:text-pink-100" />
                    </button>
                </div>

                {/* Message List */}
                <div className="flex-grow p-4 overflow-y-auto bg-white space-y-4">
                    {messages.map((msg) => (
                        <div key={msg.id} className={`flex ${msg.sender === "user" ? "justify-end" : "justify-start"}`}>
                            <div className="flex items-start max-w-[80%]">
                                {msg.sender === "bot" && (
                                    <div className="flex-shrink-0 w-7 h-7 rounded-full bg-[#EC80B1] text-white flex items-center justify-center mr-2">
                                        <FiMessageSquare size={16} />
                                    </div>
                                )}
                                <div
                                    className={`px-4 py-2 rounded-lg text-sm ${msg.sender === "user"
                                            ? "bg-[#EC80B1] text-white rounded-br-none"
                                            : "bg-gray-100 text-gray-800 rounded-bl-none"
                                        }`}
                                >
                                    {msg.sender === "bot" ? (
                                        <div
                                            className="prose prose-sm max-w-none [&_ul]:list-disc [&_ul]:pl-5 [&_li]:mb-1 [&_a]:text-pink-500 [&_a]:underline [&_strong]:font-bold [&_em]:italic [&_p]:mb-2 [&_h1]:text-xl [&_h2]:text-lg [&_h3]:text-base [&_h4]:text-sm [&_h5]:text-xs [&_h6]:text-xs [&_blockquote]:border-l-4 [&_blockquote]:border-gray-300 [&_blockquote]:pl-4 [&_blockquote]:italic"
                                            dangerouslySetInnerHTML={{ __html: md.render(msg.text) }}
                                        />
                                    ) : (
                                        msg.text
                                    )}
                                </div>
                            </div>
                        </div>
                    ))}
                    {isLoading && (
                        <div className="flex justify-start">
                            <div className="flex items-start max-w-[80%]">
                                <div className="flex-shrink-0 w-7 h-7 rounded-full bg-[#EC80B1] text-white flex items-center justify-center mr-2">
                                    <FiMessageSquare size={16} />
                                </div>
                                <div className="px-4 py-2 rounded-lg text-sm bg-gray-100 text-gray-800 rounded-bl-none">
                                    <div className="flex items-center space-x-2">
                                        <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: "0ms" }}></div>
                                        <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: "150ms" }}></div>
                                        <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: "300ms" }}></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                    <div ref={messagesEndRef} />
                </div>

                {/* Input */}
                <div className="p-3 border-t border-gray-200 bg-gray-50 rounded-b-lg">
                    <form onSubmit={handleSendMessage} className="flex items-center space-x-2">
                        <input
                            type="text"
                            value={inputValue}
                            onChange={(e) => setInputValue(e.target.value)}
                            placeholder="Message..."
                            className="flex-grow px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#EC80B1] text-sm"
                            autoComplete="off"
                            disabled={isLoading}
                        />
                        <button
                            type="submit"
                            className="p-2 bg-pink-600 text-white rounded-md hover:bg-pink-700 disabled:opacity-50 flex-shrink-0"
                            disabled={!inputValue.trim() || isLoading}
                            aria-label="Send Message"
                        >
                            <FiSend size={18} />
                        </button>
                    </form>
                </div>
            </div>
        </>
    );
};

export default ChatBotWidget;