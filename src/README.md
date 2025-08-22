        notePanel.addComponentListener(
                new java.awt.event.ComponentAdapter() {
                    @Override
                    public void componentResized(java.awt.event.ComponentEvent e) {
                        textArea.setSize(notePanel.getWidth(), notePanel.getHeight());
                    }
                }
        );